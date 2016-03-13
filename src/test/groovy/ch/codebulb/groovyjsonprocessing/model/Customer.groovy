package ch.codebulb.groovyjsonprocessing.model

import groovy.transform.*
import groovy.transform.builder.*

@CompileStatic
@TupleConstructor(includes="name")
@Builder(builderStrategy=SimpleStrategy, prefix="")
class Customer {
    String name
    Address address // to-one-relationship
    List<Purchase> purchases // to-many-relationship (generic)
    Map<DiscountType, Integer> discounts // map (generic, with enum keys)
}