package com.personal.RetrievalAugmentedGeneration.Service;

import java.io.File;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Reads text from PDF files using Spring AI's PagePdfDocumentReader.
 * Each PDF page becomes one Document (before chunking).
 */
@Service
public class PdfReaderService {
    /**
     * Reads a PDF file and returns one Document per page.
     *
     * @param pdfPath path to the PDF file on disk
     * @return list of page-level documents with page_number metadata
     */

    public List<Document> readPdf(String pdfPath) {
        Resource resource = new FileSystemResource(new File(pdfPath));

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().withPageTopMargin(0)
                .withPageExtractedTextFormatter(
                        ExtractedTextFormatter.builder().withNumberOfTopTextLinesToDelete(0).build())
                .withPagesPerDocument(1).build();

        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);
        return reader.get();

    }

}