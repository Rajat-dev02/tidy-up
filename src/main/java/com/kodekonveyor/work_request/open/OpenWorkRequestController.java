package com.kodekonveyor.work_request.open;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kodekonveyor.authentication.AuthenticatedUserService;
import com.kodekonveyor.authentication.UserEntity;
import com.kodekonveyor.webapp.ValidationException;
import com.kodekonveyor.work_request.WorkRequestConstants;
import com.kodekonveyor.work_request.WorkRequestDTO;
import com.kodekonveyor.work_request.WorkRequestEntity;
import com.kodekonveyor.work_request.WorkRequestRepository;
import com.kodekonveyor.work_request.WorkRequestStatusEnum;
import com.kodekonveyor.work_request.WorkRequestUtil;

@RestController
public class OpenWorkRequestController {

  @Autowired
  WorkRequestRepository workRequestRepository;

  @Autowired
  AuthenticatedUserService authenticatedUserService;

  @Autowired
  Logger loggerService;

  @GetMapping("/workRequest/own/@workRequestId")
  public WorkRequestDTO call(@RequestParam final long workRequestId) {
    loggerService.info(
        WorkRequestConstants.SERVICE_CALL_NAME, super.getClass().getName()
    );
    inputValidation(workRequestId);
    final WorkRequestEntity workRequestEntity =
        workRequestRepository.findByWorkRequestId(workRequestId).get(0);

    final UserEntity currentUser = authenticatedUserService.call();

    if (
      !workRequestEntity.getStatus().equals(
          WorkRequestStatusEnum.POSTED
      ) && !(currentUser.equals(workRequestEntity.getCustomer()) ||
          currentUser.equals(workRequestEntity.getProvider()))

    ) {
      loggerService.warn(
          WorkRequestConstants.WORK_REQUEST_ERROR,
          WorkRequestConstants.WORK_REQUEST_IS_NOT_POSTED,
          WorkRequestConstants.FAILURE
      );
      throw new ValidationException(
          WorkRequestConstants.WORK_REQUEST_IS_NOT_POSTED
      );
    }
    loggerService.debug(
        WorkRequestConstants.OPEN_WORK_REQUEST,
        WorkRequestConstants.SUCCESS
    );
    return WorkRequestUtil.convertWorkRequestEntityToDTO(workRequestEntity);
  }

  private void inputValidation(final long workRequestId) {

    final int workId = 0;
    if (workRequestId <= workId) {
      loggerService.warn(
          WorkRequestConstants.INPUT_VALIDATION_STATUS,
          WorkRequestConstants.NON_POSITIVE_WORK_REQUEST_ID_EXCEPTION,
          WorkRequestConstants.FAILURE
      );
      throw new ValidationException(
          WorkRequestConstants.NON_POSITIVE_WORK_REQUEST_ID_EXCEPTION
      );
    }

    final List<WorkRequestEntity> workRequestEntity =
        workRequestRepository.findByWorkRequestId(workRequestId);
    if (workRequestEntity.isEmpty()) {
      loggerService.warn(
          WorkRequestConstants.INPUT_VALIDATION_STATUS,
          WorkRequestConstants.INVALID_WORK_REQUEST_ID_EXCEPTION,
          WorkRequestConstants.FAILURE
      );
      throw new ValidationException(
          WorkRequestConstants.INVALID_WORK_REQUEST_ID_EXCEPTION
      );
    }
  }

}
