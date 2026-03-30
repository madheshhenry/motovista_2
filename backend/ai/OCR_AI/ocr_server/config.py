import string

IMG_H = 64
IMG_W = 256

BATCH_SIZE = 16
EPOCHS = 40  # 40 epochs on 100k images is plenty
LR = 0.0005  # Slightly lower initial LR for stability

# 36 chars only
CHARS = string.digits + string.ascii_uppercase  # "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
BLANK_IDX = 0  # for CTC blank

# char->index mapping (CTC needs blank)
# indices: 0=blank, 1..36 real chars
char2idx = {c: i+1 for i, c in enumerate(CHARS)}
idx2char = {i+1: c for i, c in enumerate(CHARS)}
