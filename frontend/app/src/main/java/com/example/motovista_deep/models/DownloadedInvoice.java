package com.example.motovista_deep.models;

import android.net.Uri;

public class DownloadedInvoice {
    private String fileName;
    private Uri fileUri;
    private long dateMillis;
    private String formattedSize;

    public DownloadedInvoice(String fileName, Uri fileUri, long dateMillis, String formattedSize) {
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.dateMillis = dateMillis;
        this.formattedSize = formattedSize;
    }

    public String getFileName() {
        return fileName;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public String getFormattedSize() {
        return formattedSize;
    }
}
