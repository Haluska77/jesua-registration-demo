package com.jesua.registration.service;

import com.jesua.registration.entity.ErrorType;
import com.jesua.registration.entity.ProcessingError;
import com.jesua.registration.repository.ProcessingErrorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingErrorService {
    private final ProcessingErrorRepository processingErrorRepository;

    public void createAndSaveProcessingError(String errorMessage) {
        ProcessingError processingError = new ProcessingError();
        processingError.setErrorType(ErrorType.NOTIFICATION);
        processingError.setText(errorMessage);
        processingErrorRepository.save(processingError);
    }
}
