package com.buzz.auth;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by toshikijahja on 10/22/18.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface UserAuth {

}