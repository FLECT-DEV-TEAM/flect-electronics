package model;

import helper.Salesforce;

import java.util.List;

import javax.persistence.OneToOne;

import org.junit.Test;

@SalesforceEntity
public class Asset extends SalesforceModel {
    @OneToOne
    public Contact contact;
    
    @OneToOne
    public Product2 product2;
    
    @Test
    public static List<Asset> findByContactId(String contactId) {
        return Salesforce.find(Asset.class)
            .join(Contact.class)
            .join(Product2.class)
            .where("Asset.ContactId = ?", contactId)
            .getList();
    }
}
