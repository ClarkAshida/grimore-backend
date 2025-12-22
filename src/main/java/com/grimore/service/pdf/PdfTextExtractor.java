package com.grimore.service.pdf;

import com.grimore.exception.validation.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfTextExtractor {

    public String extractText(MultipartFile file) {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            if (text == null) text = "";
            text = text.replace("\u00A0", " ");      // NBSP
            text = text.replaceAll("[ \\t]+", " ");  // normaliza espaços
            text = text.replaceAll("\\n{3,}", "\n\n");
            text = text.trim();

            if (text.isBlank()) {
                throw new BadRequestException("Não foi possível extrair texto do PDF. (Pode ser um PDF escaneado/como imagem)");
            }

            return text;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Falha ao ler PDF: " + e.getMessage());
        }
    }
}
