package com.restdude.mdd.annotation;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(" (true) ")
public @interface ModelDrivenPreAuth {
}