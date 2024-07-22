package com.oo.tools.spring.boot;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/22 16:05:05
 */
public class MyAnnotationTypeFilter extends AnnotationTypeFilter {
    public MyAnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        super(annotationType);
    }
    
    
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String className = metadataReader.getAnnotationMetadata().getClassName();
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        
        return annotationMetadata.hasAnnotation(this.getAnnotationType().getName());
    }
}
