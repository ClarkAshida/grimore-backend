package com.grimore.service.ai;

import com.grimore.dto.request.ExtractedDisciplineDTO;
import com.grimore.dto.response.ExtractedDisciplinesResponse;
import com.grimore.exception.validation.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentPdfAiExtractor {

    private final ChatClient chatClient;

    public List<ExtractedDisciplineDTO> extract(String pdfText) {
        if (pdfText == null || pdfText.isBlank()) {
            throw new BadRequestException("Texto do PDF vazio");
        }

        // evita mandar documento enorme
        String clipped = pdfText.length() > 45_000 ? pdfText.substring(0, 45_000) : pdfText;

        String systemPrompt = """
                Você é um extrator de dados do comprovante de matrícula da UFRN.
                Sua tarefa: identificar TODAS as disciplinas e retornar SOMENTE JSON válido.
                
                Regras do JSON:
                {
                  "disciplines": [
                    {
                      "name": "string",
                      "code": "AAA0000",
                      "scheduleCode": "ex: 246N12 ou 35T12 ou '35M56'",
                      "location": "string",
                      "workloadHours": "H30|H45|H60|H75|H90|H120|null"
                    }
                  ]
                }
                
                Regras:
                - code sempre 3 letras + 4 números (ex: IMD1012)
                - scheduleCode: usar M/T/N (não use V). Preserve múltiplos segmentos se existirem (separar por espaço).
                - workloadHours pode ser null (a API irá inferir pelo scheduleCode)
                - Retorne APENAS o JSON. Sem texto extra.
                """;

        String userPrompt = """
                TEXTO EXTRAÍDO DO PDF:
                ---
                %s
                ---
                """.formatted(clipped);

        ExtractedDisciplinesResponse response = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(ExtractedDisciplinesResponse.class);

        if (response == null || response.getDisciplines() == null || response.getDisciplines().isEmpty()) {
            throw new BadRequestException("Não foi possível identificar disciplinas no comprovante");
        }

        return response.getDisciplines();
    }
}
