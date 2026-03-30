import torch
import torch.nn as nn
import torch.nn.functional as F

class TinySTN(nn.Module):
    def __init__(self):
        super().__init__()
        self.localization = nn.Sequential(
            nn.Conv2d(1, 16, kernel_size=7),
            nn.MaxPool2d(2, stride=2),
            nn.ReLU(True),
            nn.Conv2d(16, 32, kernel_size=5),
            nn.MaxPool2d(2, stride=2),
            nn.ReLU(True)
        )

    def forward(self, x):
        return self.localization(x)

model = TinySTN()
x = torch.randn(1, 1, 130, 520)
out = model(x)
print("Shape:", out.shape)
print("Flattened:", out.view(1, -1).shape)
print("32 * H * W:", 32 * out.shape[2] * out.shape[3])
