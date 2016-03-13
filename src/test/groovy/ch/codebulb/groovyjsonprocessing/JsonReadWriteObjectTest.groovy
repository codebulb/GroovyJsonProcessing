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

@CompileStatic
class JsonReadWriteObjectTest {
    private static final Customer TEST_CUSTOMER = new Customer("Max")
        .address(new Address("First Street", "Los Angeles"))
        .purchases([
                new Purchase(100, Date.parse("yyyy-MM-dd", "2016-03-13"))
        ])
        .discounts([(DiscountType.FIDELITY): 5])
        
    private static final String TEST_CUSTOMER_JSON_RESULT = $/\{
    "discounts": \{
        "FIDELITY": 5
    \},
    "address": \{
        "street": "First Street",
        "country": null,
        "city": "City of Los Angeles"
    \},
    "purchases": \[
        \{
            "date": "2016-03-1[0-9]T[0-9]{2}:00:00\+0000",
            "price": 100
        \}
    \],
    "name": "Max"
\}/$
    
    private static final String TEST_CUSTOMER_JSON_INPUT = $/{
    "discounts": {
        "FIDELITY": 5
    },
    "address": {
        "street": "First Street",
        "country": null,
        "city": "City of Los Angeles"
    },
    "purchases": [
        {
            "date": "2016-03-13T00:00:00+0000",
            "price": 100
        }
    ],
    "name": "Max"
}/$
    
    @Test
    public void testWriteObject() {
        String customerJson = new JsonBuilder(TEST_CUSTOMER).toPrettyString();
        assert customerJson ==~ TEST_CUSTOMER_JSON_RESULT
    }
    
    @Test
    public void testReadObject() {
        Customer customerJson = new JsonSlurper().parseText(TEST_CUSTOMER_JSON_INPUT) as Customer
        assert customerJson instanceof Customer
        assert customerJson.name == "Max"
        
        assert customerJson.address instanceof Address
        assert customerJson.address.street == "First Street"
        assert customerJson.address.city == "City of City of Los Angeles"
        assert customerJson.address.@city == "City of Los Angeles"
        
        assert customerJson.purchases instanceof List
        assert customerJson.purchases.size() == 1
        
        assert customerJson.purchases[0] instanceof Map
        assert !(customerJson.purchases[0] instanceof Purchase)
        assert (customerJson.purchases[0] as Map).price == 100
        assert Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", (customerJson.purchases[0] as Map).date as String) == Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", "2016-03-13T00:00:00+0000")
        
        assert customerJson.discounts instanceof Map
        assert customerJson.discounts.keySet().size() == 1
        assert customerJson.discounts.keySet()[0] instanceof String
        assert customerJson.discounts.keySet()[0] == "FIDELITY"
        assert customerJson.discounts.FIDELITY == 5
    }
}
