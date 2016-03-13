package ch.codebulb.groovyjsonprocessing

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*
import ch.codebulb.groovyjsonprocessing.model.*
import groovy.json.*
import groovy.transform.CompileStatic

class JsonReadWriteTest {        
    private static final String TEST_CUSTOMERS_JSON = $/[
    {
        "name": "Max",
        "address": {
            "street": "First Street",
            "city": "Los Angeles",
            "country": "US"
        },
        "purchases": [
            {
                "date": "2016-03-13 00:00:00",
                "price": 100
            },
            {
                "date": "2016-03-13 00:00:00",
                "price": 200
            },
            {
                "date": "2016-03-13 00:00:00",
                "price": 300
            }
        ]
    },
    {
        "name": "Sarah",
        "address": {
            "street": "Second Street",
            "city": "San Francisco",
            "country": "US"
        },
        "purchases": [
            {
                "date": "2016-03-12 00:00:00",
                "price": 200
            }
        ]
    }
]/$
    
    private static final String TEST_CUSTOMERS_JSON_TRANSFORMED = $/[
    {
        "address": {
            "city": "Los Angeles",
            "country": "USA",
            "street": "First Street"
        },
        "info": {
            "currency": "USD"
        },
        "name": "Max",
        "purchases": [
            {
                "date": "2016-03-13 00:00:00",
                "price": 100
            },
            {
                "date": "2016-03-13 00:00:00",
                "price": 200
            },
            {
                "date": "2016-03-13 00:00:00",
                "price": 300
            }
        ]
    },
    {
        "address": {
            "city": "San Francisco",
            "country": "USA",
            "street": "Second Street"
        },
        "info": {
            "currency": "USD"
        },
        "name": "Sarah",
        "purchases": [
            {
                "date": "2016-03-12 00:00:00",
                "price": 200
            }
        ]
    }
]/$
    
    @Test
    public void testReadJson() {
        def root = new JsonSlurper().parseText(TEST_CUSTOMERS_JSON)
        assert root.size() == 2
        assert root[0].name == "Max"
        assert root[0].purchases[0].price == 100
        assert Date.parse("yyyy-MM-dd HH:mm:ss", root[0].purchases[0].date) == Date.parse("yyyy-MM-dd HH:mm:ss", "2016-03-13 00:00:00")
        assert root.collect {customer -> customer.purchases.findAll {it.price > 200}}.flatten()*.price == [300]
    }
    
    @Test
    public void testWriteJson() {
        def builder = new JsonBuilder()
        builder.call(
            [ 
                builder (
                    name: "Max",
                    address: address(builder, "First Street", "Los Angeles"),
                    purchases: (1..3).collect { num ->
                        builder (
                            date: "2016-03-13 00:00:00",
                            price: num * 100,
                        )
                    }
                ),
                builder (
                    name: "Sarah",
                    address: address(builder, "Second Street", "San Francisco"),
                    purchases: [
                        builder (
                            date: "2016-03-12 00:00:00",
                            price: 200,
                        )
                    ]
                )
            ]
        )
        
        assert JsonOutput.prettyPrint(builder.toString()) == TEST_CUSTOMERS_JSON
    }
    
    private static address(def builder, String street, String city) {
        return builder (
            street: street,
            city: city,
            country: "US"
        )
    }
    
    @Test
    public void testTransformJson() {
        def root = new JsonSlurper().parseText(TEST_CUSTOMERS_JSON)
        
        root.findAll { customer -> customer.address.country == "US"}.each {
            it.address.country = "USA"
            it.info = [currency: "USD"]
        }
        
        assert root[0].address.country == "USA"
        assert root[1].address.country == "USA"
        assert root[0].info.currency == "USD"
        assert root[1].info.currency == "USD"
        
        def builder = new JsonBuilder(root)
        assert JsonOutput.prettyPrint(builder.toString()) == TEST_CUSTOMERS_JSON_TRANSFORMED
    }
}
