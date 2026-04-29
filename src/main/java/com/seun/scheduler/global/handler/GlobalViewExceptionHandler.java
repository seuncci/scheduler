package com.seun.scheduler.global.handler;

import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class GlobalViewExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handEntityNotFoundException(EntityNotFoundException e) {

        return "error/404";
    }

    @ExceptionHandler(CustomException.class)
    public String handleCustomException(CustomException e, RedirectAttributes redirectAttributes) {

        ResultCode code = e.getCode();

        redirectAttributes.addFlashAttribute("errorMessage", code.getMessage());
        return "redirect:/members/me/groups";
    }
}
