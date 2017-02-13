/**
 *
 * Restdude
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.mdd.controller;

import com.restdude.mdd.annotation.controller.ModelController;
import com.restdude.mdd.registry.ModelInfo;
import com.restdude.mdd.registry.ModelInfoRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.restdude.mdd.annotation.model.ModelResource;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Maps Controllers annotated with {@link ModelController} using the
 * corresponding Model's {@link ModelResource}
 *
 * The default type level mapping is <code>{@link ModelResource#basePath()}/{@link ModelResource#parentPath()}/{@link ModelResource#pathFragment()}</code>.
 * If basePath is empty, the <code>restdude.api.basePath</code> application property is used instead.
 *
 * @see ModelController
 */
public class ModelControllerRequestMappingHandlerMapping extends RequestMappingHandlerMapping implements MatchableHandlerMapping, EmbeddedValueResolverAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelControllerRequestMappingHandlerMapping.class);

    @Value("${restdude.api.basePath}")
    private String basePath;

    @Value("${restdude.api.defaultParentPath}")
    private String defaultParentPath;

    @Value("#{'${restdude.api.prepend.excludes}'.split(',')}")
    private List<String> excludes;

    private ModelInfoRegistry modelInfoRegistry;

    private RequestMappingInfo.BuilderConfiguration shadowConfig = new RequestMappingInfo.BuilderConfiguration();

    @Autowired
    public void setModelInfoRegistry(ModelInfoRegistry modelInfoRegistry) {
        this.modelInfoRegistry = modelInfoRegistry;
    }

    /**
     * {@inheritDoc}
     * Expects a handler to have a type-level @{@link Controller} annotation.
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, ModelController.class);
    }


    /**
     * Uses method and type-level @{@link RequestMapping} annotations to create
     * the RequestMappingInfo.
     * @return the created RequestMappingInfo, or {@code null} if the method
     * does not have a {@code @RequestMapping} annotation.
     * @see #getCustomMethodCondition(Method)
     * @see #getCustomTypeCondition(Class)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo requestMappingInfo = null;


        RequestMapping methodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if(methodRequestMapping == null){
            // No mapping present, ignore method
            return null;
        }

        RequestMapping handlerRequestMapping = AnnotatedElementUtils.findMergedAnnotation(handlerType, RequestMapping.class);
        Class<?> modelType = this.modelInfoRegistry.getHandlerModelType(handlerType);
        ModelInfo modelInfo = modelType != null ? this.modelInfoRegistry.getEntryFor(modelType) : null;

        // get method mapping
        requestMappingInfo = modelInfo != null ? createRequestMappingInfo(method, methodRequestMapping, modelInfo) : null;
        if (requestMappingInfo != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType, handlerRequestMapping, modelInfo);
            if (typeInfo != null) {
                LOGGER.debug("createRequestMappingInfo typeInfo patterns: {}, for handlerType: {}", typeInfo.getPatternsCondition().getPatterns(), handlerType);
                requestMappingInfo = typeInfo.combine(requestMappingInfo);
            }
            else{
                throw new RuntimeException("Type-level RequestMappingInfo is null for handlerType: " + handlerType);
            }
        }
        else{
            throw new RuntimeException("Method-level RequestMappingInfo is null for handlerType: " + handlerType + ", method: " +method);
        }

        return requestMappingInfo;
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element, RequestMapping requestMapping, ModelInfo modelInfo) {

        if(requestMapping != null){
            LOGGER.debug("createRequestMappingInfo, element: {}, modelType: {}", element, modelInfo.getModelType());
            RequestCondition<?> condition = (element instanceof Class ?
                    getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
            String[] unexpandedPatterns = requestMapping.value();
            String[] expandedPatterns = this.expandPatterns(modelInfo, unexpandedPatterns);
            LOGGER.debug("createRequestMappingInfo, element: {}, unexpandedPatterns: {}, expandedPatterns: {}", element, unexpandedPatterns, expandedPatterns);
            return this.createRequestMappingInfo(requestMapping, expandedPatterns, condition);
        }
        else{
            LOGGER.warn("createRequestMappingInfo, no RequestMapping found for element: {}, modelType: {}", element, modelInfo.getModelType());
            return null;
        }

    }


    /**
     * Create a {@link RequestMappingInfo} from the supplied
     * {@link RequestMapping @RequestMapping} annotation, which is either
     * a directly declared annotation, a meta-annotation, or the synthesized
     * result of merging annotation attributes within an annotation hierarchy.
     */
    protected RequestMappingInfo createRequestMappingInfo(
            RequestMapping requestMapping, String[] expandedPatterns, RequestCondition<?> customCondition) {
        return RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(expandedPatterns))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name())
                .customCondition(customCondition)
                .options(this.shadowConfig)
                .build();
    }

    @Override
    public void afterPropertiesSet() {
        LOGGER.debug("afterPropertiesSet, basePath: {}, defaultParentPath: {}, excludes: {}", this.basePath, this.defaultParentPath, this.excludes);
        this.shadowConfig = new RequestMappingInfo.BuilderConfiguration();
        this.shadowConfig.setUrlPathHelper(getUrlPathHelper());
        this.shadowConfig.setPathMatcher(getPathMatcher());
        this.shadowConfig.setSuffixPatternMatch(useSuffixPatternMatch());
        this.shadowConfig.setTrailingSlashMatch(useTrailingSlashMatch());
        this.shadowConfig.setRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch());
        this.shadowConfig.setContentNegotiationManager(getContentNegotiationManager());

        initHandlerMethods();//super.afterPropertiesSet();
    }


    /**
     * Resolve placeholder values in the given array of patterns.
     * @return a new array with updated patterns
     */
    protected String[] expandPatterns(ModelInfo modelInfo , String[] patterns) {
        if(ArrayUtils.isNotEmpty(patterns)){

            String[] patternsNew = new String[patterns.length];
            String modelUriComponent = modelInfo.getUriComponent();
            String modelParentPath = modelInfo.getParentPath(StringUtils.isNotBlank(this.defaultParentPath) ? this.defaultParentPath : "");
            String modelBasePath = modelInfo.getBasePath(this.basePath);

            LOGGER.debug("expandPatterns, modelType: {}, modelBasePath: {}, modelParentPath: {}, modelUriComponent: {}", modelInfo.getModelType(), modelBasePath, modelParentPath, modelUriComponent);
            // process patterns
            for(int i = 0; i < patterns.length; i++){
                String pattern = patterns[i];
                if(isPatternIncluded(pattern)){
                    pattern = pattern.replace(ModelController.MODEL_MAPPING_WILDCARD, "/" + ModelController.BASE_PATH_WILDCARD + "/" + ModelController.APP_PARENT_PATH_WILDCARD + "/" + ModelController.MODEL_URI_COMPONENT_WILDCARD);
                    pattern = pattern.replace(ModelController.BASE_PATH_WILDCARD, "/" + modelBasePath);
                    pattern = pattern.replace(ModelController.APP_PARENT_PATH_WILDCARD, "/" + modelParentPath);
                    pattern = pattern.replace(ModelController.MODEL_URI_COMPONENT_WILDCARD, "/" + modelUriComponent);
                    pattern = pattern.replaceAll("/{2,}", "/");
                }
                patternsNew[i] = pattern;
            }
            patterns = patternsNew;
        }
        return patterns;
    }

    private boolean isPatternIncluded(String pattern) {
        if(CollectionUtils.isNotEmpty(this.excludes)){
            for(String exclude : this.excludes){
                if(pattern.startsWith(exclude)){
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Return the file extensions to use for suffix pattern matching.
     */
    @Override
    public List<String> getFileExtensions() {
        return this.shadowConfig.getFileExtensions();
    }


    @RequestMapping(value = ModelController.MODEL_MAPPING_WILDCARD)
    protected static class DefaultRequestMappingAnnotatedElement{

    }

}