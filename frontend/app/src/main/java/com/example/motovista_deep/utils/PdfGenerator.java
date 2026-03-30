package com.example.motovista_deep.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PdfGenerator {

    private Context context;
    private static final String TAG = "PdfGenerator";

    // Fonts
    private Font titleFont;
    private Font subtitleFont;
    private Font normalFont;
    private Font boldFont;
    private Font headerFont;

    public PdfGenerator(Context context) {
        this.context = context;
        initializeFonts();
    }

    private void initializeFonts() {
        try {
            // Using iText's built-in fonts
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing fonts: " + e.getMessage());
        }
    }

    public File generateBillReceipt(HashMap<String, String> data) {
        Document document = new Document(PageSize.A4);
        String fileName = "Bill_Receipt_" + data.get("transaction_id") + ".pdf";

        try {
            File file = createPdfFile(fileName);
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();
            addBillReceiptContent(document, data);
            document.close();

            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error generating bill receipt: " + e.getMessage());
            return null;
        }
    }

    public File generateDeliveryNote(HashMap<String, String> data) {
        Document document = new Document(PageSize.A4);
        String fileName = "Delivery_Note_" + data.get("transaction_id") + ".pdf";

        try {
            File file = createPdfFile(fileName);
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();
            addDeliveryNoteContent(document, data);
            document.close();

            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error generating delivery note: " + e.getMessage());
            return null;
        }
    }

    private void addBillReceiptContent(Document document, HashMap<String, String> data) throws DocumentException {
        // Company Header
        Paragraph companyHeader = new Paragraph("MOTOVISTA DEEP", titleFont);
        companyHeader.setAlignment(Element.ALIGN_CENTER);
        companyHeader.setSpacingAfter(10);
        document.add(companyHeader);

        // Title
        Paragraph title = new Paragraph("BILL / CASH RECEIPT", subtitleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Status Badge
        Paragraph status = new Paragraph("PAID", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GREEN));
        status.setAlignment(Element.ALIGN_CENTER);
        status.setSpacingAfter(30);
        document.add(status);

        // Transaction Details Table
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingBefore(20);
        detailsTable.setSpacingAfter(20);

        addTableHeader(detailsTable, "Field");
        addTableHeader(detailsTable, "Details");

        addTableRow(detailsTable, "Transaction ID", data.get("transaction_id"));
        addTableRow(detailsTable, "Date", new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(new Date()));
        addTableRow(detailsTable, "Customer Name", data.get("customer_name"));
        addTableRow(detailsTable, "Vehicle Model", data.get("vehicle_model"));
        addTableRow(detailsTable, "Payment Mode", data.get("payment_mode"));
        addTableRow(detailsTable, "Contact Number", data.get("contact_number"));
        addTableRow(detailsTable, "Email", data.get("email"));

        document.add(detailsTable);

        // Payment Breakdown Table
        Paragraph paymentTitle = new Paragraph("PAYMENT BREAKDOWN", subtitleFont);
        paymentTitle.setSpacingBefore(30);
        paymentTitle.setSpacingAfter(10);
        document.add(paymentTitle);

        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setSpacingBefore(10);
        paymentTable.setSpacingAfter(20);

        addTableHeader(paymentTable, "Description");
        addTableHeader(paymentTable, "Amount (₹)");

        double vehiclePrice = Double.parseDouble(data.getOrDefault("vehicle_price", "0"));
        double gst = Double.parseDouble(data.getOrDefault("gst_amount", "0"));
        double insurance = Double.parseDouble(data.getOrDefault("insurance_amount", "0"));
        double registration = Double.parseDouble(data.getOrDefault("registration_fee", "0"));
        double discount = Double.parseDouble(data.getOrDefault("discount", "0"));
        double paidAmount = Double.parseDouble(data.getOrDefault("amount_paid", "0"));

        addTableRow(paymentTable, "Vehicle Price", formatCurrency(vehiclePrice));
        addTableRow(paymentTable, "GST (18%)", formatCurrency(gst));
        addTableRow(paymentTable, "Insurance", formatCurrency(insurance));
        addTableRow(paymentTable, "Registration Fee", formatCurrency(registration));
        addTableRow(paymentTable, "Discount", formatCurrency(discount));

        // Total row
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL AMOUNT", boldFont));
        totalLabelCell.setBorder(PdfPCell.NO_BORDER);
        PdfPCell totalValueCell = new PdfPCell(new Phrase(formatCurrency(vehiclePrice + gst + insurance + registration - discount), boldFont));
        totalValueCell.setBorder(PdfPCell.NO_BORDER);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        paymentTable.addCell(totalLabelCell);
        paymentTable.addCell(totalValueCell);

        // Amount Paid row
        PdfPCell paidLabelCell = new PdfPCell(new Phrase("AMOUNT PAID", boldFont));
        paidLabelCell.setBorder(PdfPCell.NO_BORDER);
        PdfPCell paidValueCell = new PdfPCell(new Phrase(formatCurrency(paidAmount), boldFont));
        paidValueCell.setBorder(PdfPCell.NO_BORDER);
        paidValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        paymentTable.addCell(paidLabelCell);
        paymentTable.addCell(paidValueCell);

        document.add(paymentTable);

        // EMI Details if applicable
        if (data.containsKey("emi_details") && !data.get("emi_details").isEmpty()) {
            Paragraph emiTitle = new Paragraph("EMI DETAILS", subtitleFont);
            emiTitle.setSpacingBefore(20);
            emiTitle.setSpacingAfter(10);
            document.add(emiTitle);

            Paragraph emiDetails = new Paragraph(data.get("emi_details"), normalFont);
            document.add(emiDetails);
        }

        // Footer
        Paragraph footer = new Paragraph("\n\nThank you for your business!\n" +
                "For any queries, contact: support@motovistadeep.com\n" +
                "Phone: +91 9876543210\n" +
                "This is a computer-generated receipt.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    private void addDeliveryNoteContent(Document document, HashMap<String, String> data) throws DocumentException {
        // Company Header
        Paragraph companyHeader = new Paragraph("MOTOVISTA DEEP", titleFont);
        companyHeader.setAlignment(Element.ALIGN_CENTER);
        companyHeader.setSpacingAfter(10);
        document.add(companyHeader);

        // Title
        Paragraph title = new Paragraph("VEHICLE DELIVERY NOTE", subtitleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Authorization Statement
        Paragraph authStatement = new Paragraph("VEHICLE HANDOVER AUTHORIZATION", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
        authStatement.setAlignment(Element.ALIGN_CENTER);
        authStatement.setSpacingAfter(20);
        document.add(authStatement);

        // Delivery Details
        PdfPTable deliveryTable = new PdfPTable(2);
        deliveryTable.setWidthPercentage(100);
        deliveryTable.setSpacingBefore(20);
        deliveryTable.setSpacingAfter(20);

        addTableHeader(deliveryTable, "Field");
        addTableHeader(deliveryTable, "Details");

        addTableRow(deliveryTable, "Delivery Note No.", "DN-" + new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date()));
        addTableRow(deliveryTable, "Transaction ID", data.get("transaction_id"));
        addTableRow(deliveryTable, "Delivery Date", new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
        addTableRow(deliveryTable, "Customer Name", data.get("customer_name"));
        addTableRow(deliveryTable, "Vehicle Model", data.get("vehicle_model"));
        addTableRow(deliveryTable, "Chassis No.", data.getOrDefault("chassis_number", "To be filled"));
        addTableRow(deliveryTable, "Engine No.", data.getOrDefault("engine_number", "To be filled"));
        addTableRow(deliveryTable, "Vehicle Color", data.getOrDefault("vehicle_color", "To be filled"));
        addTableRow(deliveryTable, "Registration No.", data.getOrDefault("registration_number", "Pending"));

        document.add(deliveryTable);

        // Checklist Section
        Paragraph checklistTitle = new Paragraph("VEHICLE HANDOVER CHECKLIST", subtitleFont);
        checklistTitle.setSpacingBefore(30);
        checklistTitle.setSpacingAfter(10);
        document.add(checklistTitle);

        PdfPTable checklistTable = new PdfPTable(3);
        checklistTable.setWidthPercentage(100);
        checklistTable.setSpacingBefore(10);

        addTableHeader(checklistTable, "Item");
        addTableHeader(checklistTable, "Status");
        addTableHeader(checklistTable, "Remarks");

        String[] checklistItems = {
                "Original RC Book",
                "Insurance Certificate",
                "Spare Key",
                "Tool Kit",
                "Service Manual",
                "Vehicle Cleanliness",
                "Function Test (All lights)",
                "Function Test (Horn)",
                "Function Test (Brakes)",
                "Fuel Level"
        };

        for (String item : checklistItems) {
            checklistTable.addCell(new PdfPCell(new Phrase(item, normalFont)));
            checklistTable.addCell(new PdfPCell(new Phrase("✓", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GREEN))));
            checklistTable.addCell(new PdfPCell(new Phrase("Checked and Verified", normalFont)));
        }

        document.add(checklistTable);

        // Gate Pass Section
        Paragraph gatePassTitle = new Paragraph("GATE PASS", subtitleFont);
        gatePassTitle.setSpacingBefore(30);
        gatePassTitle.setSpacingAfter(10);
        document.add(gatePassTitle);

        PdfPTable gatePassTable = new PdfPTable(2);
        gatePassTable.setWidthPercentage(100);
        gatePassTable.setSpacingBefore(10);

        addTableRow(gatePassTable, "Authorized By", "____________________");
        addTableRow(gatePassTable, "Designation", "Showroom Manager");
        addTableRow(gatePassTable, "Received By", "____________________");
        addTableRow(gatePassTable, "Customer Signature", "____________________");
        addTableRow(gatePassTable, "Date & Time", new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(new Date()));
        addTableRow(gatePassTable, "Security Stamp", "[STAMP HERE]");

        document.add(gatePassTable);

        // Important Notes
        Paragraph notes = new Paragraph("\n\nIMPORTANT NOTES:\n" +
                "1. Please verify all documents before leaving the showroom.\n" +
                "2. First service due within 30 days or 500 km, whichever is earlier.\n" +
                "3. Keep all documents safe for future reference.\n" +
                "4. In case of emergency, contact roadside assistance: 1800-XXX-XXXX",
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.RED));
        document.add(notes);

        // Footer
        Paragraph footer = new Paragraph("\n\nThis delivery note serves as official proof of vehicle handover.\n" +
                "MOTOVISTA DEEP - Customer Satisfaction is Our Priority",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
        cell.setBackgroundColor(new BaseColor(19, 200, 236)); // Primary color
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private String formatCurrency(double amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            return formatter.format(amount);
        } catch (Exception e) {
            return "₹" + amount;
        }
    }

    private File createPdfFile(String fileName) throws IOException {
        File storageDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, use app-specific directory
            storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MotovistaPDFs");
        } else {
            // For older versions, use Downloads folder
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MotovistaPDFs");
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return new File(storageDir, fileName);
    }
}