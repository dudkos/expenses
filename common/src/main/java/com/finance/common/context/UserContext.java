package com.finance.common.context;

import com.finance.common.exception.ServiceException;

public interface UserContext {

    Integer getUserId() throws ServiceException;

    String getUserName();
}
