package ch.codebulb.groovyjsonprocessing.model

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor
class Purchase {
    int price
    Date date
}