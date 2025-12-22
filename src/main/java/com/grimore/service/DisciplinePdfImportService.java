package com.grimore.service;

import com.grimore.dto.request.ExtractedDisciplineDTO;
import com.grimore.dto.response.BatchCreateReportDTO;
import com.grimore.dto.response.ImportDisciplinesResultDTO;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.service.ai.EnrollmentPdfAiExtractor;
import com.grimore.service.pdf.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DisciplinePdfImportService {

    private final PdfTextExtractor pdfTextExtractor;
    private final EnrollmentPdfAiExtractor enrollmentPdfAiExtractor;
    private final DisciplineService disciplineService;

    public ImportDisciplinesResultDTO importEnrollmentPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Arquivo PDF é obrigatório");
        }

        // content-type às vezes vem null; então só valida se vier preenchido
        if (file.getContentType() != null
                && !Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
            throw new BadRequestException("Arquivo inválido. Envie um PDF.");
        }

        String text = pdfTextExtractor.extractText(file);
        List<ExtractedDisciplineDTO> extracted = enrollmentPdfAiExtractor.extract(text);

        BatchCreateReportDTO report = disciplineService.createBatchFromExtractedWithReport(extracted);

        return new ImportDisciplinesResultDTO(
                extracted.size(),
                report.created().size(),
                report.created(),
                report.errors()
        );
    }
}
