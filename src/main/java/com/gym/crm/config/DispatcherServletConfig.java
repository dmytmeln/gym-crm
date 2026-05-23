package com.gym.crm.config;

import com.gym.crm.GymCrmApplication;
import com.gym.crm.logging.RequestLoggingFilter;
import com.gym.crm.logging.TransactionLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{GymCrmApplication.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected @NonNull String[] getServletMappings() {
        return new String[]{"/gym-crm/*"};
    }

    @Nullable
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new TransactionLoggingFilter(),
                new RequestLoggingFilter()
        };
    }

}
