import torch
import torch.nn as nn
import torch.nn.functional as F

class STN(nn.Module):
    def __init__(self, in_h=130, in_w=520):
        super(STN, self).__init__()
        self.localization = nn.Sequential(
            nn.Conv2d(1, 16, kernel_size=7),
            nn.MaxPool2d(2, stride=2),
            nn.ReLU(True),
            nn.Conv2d(16, 32, kernel_size=5),
            nn.MaxPool2d(2, stride=2),
            nn.ReLU(True)
        )
        
        # Dynamic calculation of linear input size
        with torch.no_grad():
            dummy = torch.zeros(1, 1, in_h, in_w)
            flatten_size = self.localization(dummy).view(1, -1).size(1)

        self.fc_loc = nn.Sequential(
            nn.Linear(flatten_size, 64),
            nn.ReLU(True),
            nn.Linear(64, 3 * 2)
        )
        self.fc_loc[2].weight.data.zero_()
        self.fc_loc[2].bias.data.copy_(torch.tensor([1, 0, 0, 0, 1, 0], dtype=torch.float))

    def forward(self, x):
        xs = self.localization(x)
        xs = xs.view(xs.size(0), -1)
        theta = self.fc_loc(xs)
        theta = theta.view(-1, 2, 3)
        grid = F.affine_grid(theta, x.size(), align_corners=True)
        x = F.grid_sample(x, grid, align_corners=True)
        return x

class ResBlock(nn.Module):
    def __init__(self, in_channels, out_channels, stride=1):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels, out_channels, 3, stride, 1, bias=False)
        self.bn1 = nn.BatchNorm2d(out_channels)
        self.conv2 = nn.Conv2d(out_channels, out_channels, 3, 1, 1, bias=False)
        self.bn2 = nn.BatchNorm2d(out_channels)
        self.shortcut = nn.Sequential()
        if stride != 1 or in_channels != out_channels:
            self.shortcut = nn.Sequential(
                nn.Conv2d(in_channels, out_channels, 1, stride, bias=False),
                nn.BatchNorm2d(out_channels)
            )

    def forward(self, x):
        out = F.relu(self.bn1(self.conv1(x)))
        out = self.bn2(self.conv2(out))
        out += self.shortcut(x)
        return F.relu(out)

class CRNN(nn.Module):
    def __init__(self, num_classes, in_h=130, in_w=520):
        super().__init__()
        self.stn = STN(in_h, in_w)
        
        # Robust ResNet feature extractor
        self.cnn = nn.Sequential(
            nn.Conv2d(1, 64, kernel_size=3, stride=1, padding=1, bias=False),
            nn.BatchNorm2d(64),
            nn.ReLU(True),
            ResBlock(64, 128, stride=2),    # Lower resolution H/2, W/2
            ResBlock(128, 256, stride=2),   # H/4, W/4
            ResBlock(256, 512, stride=(2, 1)), # H/8, W/4
            ResBlock(512, 512, stride=(2, 1)), # H/16, W/4
        )

        self.pool = nn.AdaptiveAvgPool2d((1, None))

        self.rnn = nn.LSTM(
            input_size=512,
            hidden_size=256,
            num_layers=2,
            bidirectional=True,
            batch_first=True
        )

        self.fc = nn.Linear(512, num_classes)

    def forward(self, x):
        # x: (B,1,130,520)
        x = self.stn(x)
        feat = self.cnn(x)          # (B,512,H',W')
        feat = self.pool(feat)      # (B,512,1,W')
        feat = feat.squeeze(2)      # (B,512,W')
        feat = feat.permute(0, 2, 1)  # (B,W',512)

        out, _ = self.rnn(feat)     # (B,W',512)
        out = self.fc(out)          # (B,W',num_classes)
        out = out.permute(1, 0, 2)  # (T,B,C)
        return out
