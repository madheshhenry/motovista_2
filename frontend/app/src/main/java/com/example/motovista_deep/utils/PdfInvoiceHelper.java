package com.example.motovista_deep.utils;

import android.content.Context;
import android.util.Log;

import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.models.CustomFitting;
import com.example.motovista_deep.models.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.List;

public class PdfInvoiceHelper {

    private static final String TAG = "PdfInvoiceHelper";
    private static final BaseColor THEME_BLUE = new BaseColor(0, 51, 153);

    public static void generateInvoice(Context context, OutputStream outputStream, BikeModel bike, User user, List<CustomFitting> selectedFittings, String totalPrice) {
        Document document = new Document(PageSize.A4, 20, 20, 30, 20);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Fonts
            Font logoFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD | Font.ITALIC, THEME_BLUE);
            Font companyNameFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, THEME_BLUE);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, THEME_BLUE);
            Font headerSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, THEME_BLUE);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, THEME_BLUE);

            // 1. Header Section (Logo | Company Details | Contacts)
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1.5f, 5, 2});

            // Logo
            PdfPCell logoCell = new PdfPCell(new Phrase("sb", logoFont));
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);

            // Company Info
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.addElement(new Paragraph("SANTHOSH BIKES", companyNameFont));
            infoCell.addElement(new Paragraph("(House of Two Wheelers)", subTitleFont));
            infoCell.addElement(new Paragraph("No.433, C.T.H. Road, Kavarapalayam, Avadi, Chennai - 54.", headerSmallFont));
            infoCell.addElement(new Paragraph("(Near Anchaneyar Temple)", headerSmallFont));
            headerTable.addCell(infoCell);

            // Contact Numbers
            PdfPCell contactCell = new PdfPCell();
            contactCell.setBorder(Rectangle.NO_BORDER);
            contactCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph pContact = new Paragraph("81229 99809\n76048 38750", headerSmallFont);
            pContact.setAlignment(Element.ALIGN_RIGHT);
            contactCell.addElement(pContact);
            headerTable.addCell(contactCell);

            document.add(headerTable);

            // 2. Customer Line
            String nameToDisplay = user != null ? user.getFull_name() : "";
            Paragraph pMs = new Paragraph("M/s. " + nameToDisplay + " ....................................................................................................................................................", normalFont);
            pMs.setSpacingBefore(10);
            document.add(pMs);

            document.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------------------------------------------------------"));

            // 3. Proforma Invoice Title
            Paragraph title = new Paragraph("PROFORMA INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(5);
            title.setSpacingAfter(10);
            document.add(title);

            // 4. Main Table
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{1.5f, 1});

            // VEHICLE Row (Colspan)
            addMainCell(mainTable, "VEHICLE", tableHeaderFont, 2, true);

            // Bike Info Row (Colspan)
            String bikeInfo = (bike != null) ? (bike.getBrand() + " " + bike.getModel() + " (" + bike.getVariant() + ")") : "";
            PdfPCell bikeCell = new PdfPCell(new Phrase(bikeInfo, boldFont));
            bikeCell.setColspan(2);
            bikeCell.setPadding(8);
            mainTable.addCell(bikeCell);

            // Cost Description 1
            addMainCell(mainTable, "COST OF VEHICLE (Including Excise Duty, Transit Insurance,\nTransportation, Handling Charges etc.)", smallFont, 2, false);
            
            // Cost Description 2
            addMainCell(mainTable, "Registration, Comprehensive Insurance, Life Tax and Incidental Charge etc.", smallFont, 2, false);

            // Headers for Split Section
            addMainCell(mainTable, "MANDATORY FITTINGS", tableHeaderFont, 1, false);
            addMainCell(mainTable, "INCLUDING", tableHeaderFont, 1, false);

            // Data rows for fittings and "INCLUDING" items
            int maxRows = 12; // Typical rows in the template
            int fittingCount = 1;
            
            // Mandatory List
            List<CustomFitting> mandFittings = (bike != null) ? bike.getMandatoryFittings() : null;
            
            // "INCLUDING" Items List
            String[] includingItems = {"RTO Charges", "5 Yrs. Insurance", "Life & Road Tax", "Online Fees", "GST & Smart Card Charges"};

            for (int i = 0; i < maxRows; i++) {
                // Left Cell: Mandatory Fitting
                String mandText = "";
                if (mandFittings != null && i < mandFittings.size()) {
                    mandText = "(" + (fittingCount++) + ") " + mandFittings.get(i).getName();
                } else if (mandFittings != null && i == mandFittings.size() && (selectedFittings == null || selectedFittings.isEmpty())) {
                    // Start of additional section if no additional selected
                    // mandText = "ADDITIONAL FITTINGS"; // We'll handle this as a header below
                }
                
                PdfPCell leftCell = new PdfPCell(new Phrase(mandText, smallFont));
                leftCell.setPadding(4);
                mainTable.addCell(leftCell);

                // Right Cell: Including Item
                String incText = (i < includingItems.length) ? includingItems[i] : "";
                PdfPCell rightCell = new PdfPCell(new Phrase(incText, smallFont));
                rightCell.setPadding(4);
                mainTable.addCell(rightCell);
            }

            // ADDITIONAL FITTINGS Header
            addMainCell(mainTable, "ADDITIONAL FITTINGS", tableHeaderFont, 1, false);
            addMainCell(mainTable, "", normalFont, 1, false);

            // Additional Fittings List
            int additionalMax = 10;
            for (int i = 0; i < additionalMax; i++) {
                // Left Cell: Additional Fitting
                String addText = "";
                if (selectedFittings != null && i < selectedFittings.size()) {
                    CustomFitting f = selectedFittings.get(i);
                    String priceStr = (f.getPrice() == null || f.getPrice().isEmpty() || f.getPrice().equalsIgnoreCase("Included")) ? "Included" : "₹ " + f.getPrice();
                    addText = "(" + (fittingCount++) + ") " + f.getName() + " - " + priceStr;
                }
                PdfPCell leftCell = new PdfPCell(new Phrase(addText, smallFont));
                leftCell.setPadding(4);
                mainTable.addCell(leftCell);

                // Right Cell: Empty or Total at the end
                if (i == additionalMax - 2) {
                    addMainCell(mainTable, "TOTAL ON-ROAD PRICE", tableHeaderFont, 1, false);
                } else if (i == additionalMax - 1) {
                    PdfPCell totalValCell = new PdfPCell(new Phrase(totalPrice, boldFont));
                    totalValCell.setPadding(6);
                    totalValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    mainTable.addCell(totalValCell);
                } else {
                    mainTable.addCell(new PdfPCell(new Phrase("", smallFont)));
                }
            }

            document.add(mainTable);

            // 5. Documentation Section
            Paragraph pDocHeader = new Paragraph("For Registration purpose any one of the following is required in ORIGINAL & XEROX for Address Proof.", headerSmallFont);
            pDocHeader.setSpacingBefore(10);
            document.add(pDocHeader);

            PdfPTable docTable = new PdfPTable(4);
            docTable.setWidthPercentage(100);
            docTable.setSpacingBefore(5);
            addDocCell(docTable, "(1) Aadhar Card", smallFont);
            addDocCell(docTable, "(2) LIC Policy", smallFont);
            addDocCell(docTable, "(3) Passport", smallFont);
            addDocCell(docTable, "(4) G.S.T.", smallFont);
            addDocCell(docTable, "(5) Voter I.D.", smallFont);
            addDocCell(docTable, "(6) PAN Card", smallFont);
            addDocCell(docTable, "", smallFont);
            addDocCell(docTable, "", smallFont);
            document.add(docTable);

            // 6. Footer Notes
            Paragraph pPriceNote = new Paragraph("Note : PRICE & TAXES RULING AT THE TIME OF DELIVERY WILL BE APPLICABLE", boldFont);
            pPriceNote.setSpacingBefore(10);
            document.add(pPriceNote);

            // 7. Bank Details & Signatory
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);

            // Bank Details Box
            PdfPCell bankBox = new PdfPCell();
            bankBox.setBorder(Rectangle.BOX);
            bankBox.setPadding(8);
            bankBox.addElement(new Paragraph("Bank Details :", boldFont));
            bankBox.addElement(new Paragraph("Account Name : SANTHOSH BIKES", smallFont));
            bankBox.addElement(new Paragraph("A/c No. : 75010200000585 / IFSC : BARB0VJAVAD", smallFont));
            bankBox.addElement(new Paragraph("Bank Name : Bank of Baroda / Avadi Branch", smallFont));
            footerTable.addCell(bankBox);

            // Signatory
            PdfPCell signCell = new PdfPCell();
            signCell.setBorder(Rectangle.NO_BORDER);
            Paragraph pFor = new Paragraph("For SANTHOSH BIKES", boldFont);
            pFor.setAlignment(Element.ALIGN_RIGHT);
            signCell.addElement(pFor);
            signCell.addElement(new Paragraph("\n\n"));
            Paragraph pAuthorised = new Paragraph("Authorised Signatory", normalFont);
            pAuthorised.setAlignment(Element.ALIGN_RIGHT);
            signCell.addElement(pAuthorised);
            footerTable.addCell(signCell);

            document.add(footerTable);

            // 8. Motto
            Paragraph pMotto = new Paragraph("LIVE & LET ALIVE", boldFont);
            pMotto.setAlignment(Element.ALIGN_CENTER);
            pMotto.setSpacingBefore(10);
            document.add(pMotto);

            document.close();
        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF", e);
        }
    }

    private static void addMainCell(PdfPTable table, String text, Font font, int colspan, boolean center) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setPadding(6);
        if (center) cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addDocCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2);
        table.addCell(cell);
    }
}
