package ch.codebulb.groovyjsonprocessing.model

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor
class Address {
    String street
    String city
    String country
    
    public String getCity() {
        return "City of $city"
    }
}