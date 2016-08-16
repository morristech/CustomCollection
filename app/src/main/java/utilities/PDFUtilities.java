package utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import data.Collection;
import data.CollectionItem;
import data.CollectionItemPhoto;
import data.DatabaseHelper;

public class PDFUtilities {
    public static void writePDF(Context context, int collectionId) throws DocumentException, FileNotFoundException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Writing PDF");
        dialog.setMessage("Please Wait While I Write Your PDF...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        Document document = new Document();
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File pdf = new File(exportDir, "collections_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        writer.setStrictImageSequence(true);
        document.open();
        document.setMargins(50, 45, 50, 60);
        Collection collection = databaseHelper.getCollectionByCollectionId(collectionId);
        document.addTitle(collection.getTitle());
        document.addSubject("Listing items from " + collection.getTitle());
        document.addKeywords(collection.getTitle());

        Paragraph titlePage = new Paragraph();
        titlePage.setAlignment(Element.ALIGN_CENTER);
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 60.0f, Font.BOLD, BaseColor.BLACK);
        Font f2 = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.NORMAL, BaseColor.BLACK);
        titlePage.setFont(f);
        LineSeparator sep = new LineSeparator();
        titlePage.add(collection.getTitle());
        addEmptyLine(titlePage, 1);
        titlePage.add(new Chunk(sep));
        addEmptyLine(titlePage, 2);
        document.add(titlePage);
        for (CollectionItem item : collection.getItems()) {
            item.populateScaledBitmapsFromUri(context);
        }
        for (CollectionItem item : collection.getItems()) {
            Paragraph itemParagraph = new Paragraph();
            itemParagraph.setFont(f2);
            if (!item.getPhotos().isEmpty()) {
                itemParagraph.setAlignment(Element.ALIGN_CENTER);
                itemParagraph.add("Name: " + item.getName());
                itemParagraph.setAlignment(Element.ALIGN_LEFT);
                addEmptyLine(itemParagraph, 1);
                int count = 1;
                for (CollectionItemPhoto photo: item.getPhotos()) {
                    try {
                        itemParagraph.add("Photo " + count + ": ");
                        addEmptyLine(itemParagraph, 1);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.getPhotosAsBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        itemParagraph.add(image);
                    } catch (IOException ex) {
                        return;
                    }
                }
            }
            addEmptyLine(itemParagraph, 2);
            itemParagraph.add("Description: " + item.getDescription());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Value: $" + item.getValue() + "");
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Index: " + item.getCustomIndexReminder());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Material: " + item.getMaterial().getName());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add(new Chunk(sep));
            addEmptyLine(itemParagraph, 1);

            document.add(itemParagraph);
        }
        document.close();
        dialog.dismiss();
        databaseHelper.close();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
