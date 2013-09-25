package model;

import helper.Salesforce;
import internal.SalesforceFind.Order;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

@SalesforceEntity
public class Product2 extends SalesforceModel {

    @Id
    @Column
    public String id;

    @Column
    public String name;
    
    @Column
    public String productCode;
    
    @Column
    public String family;
    
    @Column
    public String subFamily__c;
    
    @Column
    public String isActive; 
    
    @Column
    public String familyEn__c;
    
    public static List<Product2> findProductsByFamilyAndSubFamily(String family, String subFamily) {
        return Salesforce
            .find(Product2.class)
            .where("Product2.Family = ? AND Product2.SubFamily__c = ? ", family, subFamily)
            .orderBy("Product2.ProductCode", Order.ASC)
            .getList();
    }
    
    public static Product2 findByProductCode(String productCode) {
        return Salesforce
                .find(Product2.class)
                .where("Product2.productCode = ? ", productCode)
                .getSingle();
    }
    
    public static List<Product2> findAllOrdered() {
        return Salesforce
                .find(Product2.class)
                .where("Product2.IsActive = ?", "true")
                .orderBy("Product2.ProductCode", Order.ASC)
                .getList();
    }

    public static Product2 findProductsByFamilyEn(String family) {
        if(family == null) {
            family = "tv";
        }
        return Salesforce
                .find(Product2.class)
                .where("Product2.FamilyEn__c = ?", family)
                .orderBy("Product2.ProductCode", Order.ASC)
                .limit(1)
                .getSingle();
    }
    
    public static List<Product2> findProductsByFamily(String family) {
        if(family == null) {
            family = "テレビ";
        }
        return Salesforce
                .find(Product2.class)
                .where("Product2.Family = ?", family)
                .orderBy("Product2.ProductCode", Order.ASC)
                .getList();
    }
    
    public enum Family {
        TELEVISION("テレビ", "tv"
                , EnumSet.of(SubFamily.PLASMA_TV, SubFamily.LCD_TV))
        ,RECORDER("レコーダー", "recorder"
                , EnumSet.of(SubFamily.BLURAY_RECORDER, SubFamily.DVD_RECORDER))
        ,DEGITAL_CAMERA("ヘッドホン", "headphone"
                , EnumSet.of(SubFamily.NOISE_CANCELING_HEADPHONE, SubFamily.AUDIO_HEADPHONE, SubFamily.MOBILE_HEADPHONE))
        ,HEADPHONE("デジタルカメラ", "camera"
                , EnumSet.of(SubFamily.SINGLE_LENS_DIGITAL_CAMERA, SubFamily.COMPACT_DIGITAL_CAMERA))
        ,PERIPHERAL("周辺機器", "etc"
                , EnumSet.of(SubFamily.HDMI_CABLE, SubFamily.DVI_CABLE, SubFamily.AV_CABLE));
        private String name;
        private String enName;
        private EnumSet<SubFamily> subFamilies;
        private Family(String name, String enName, EnumSet<SubFamily> subFamilies) {
            this.name = name;
            this.enName = enName;
            this.subFamilies = subFamilies;
        }
        public String getName() {
            return this.name;
        }
        public String getEnName() {
            return this.enName;
        }
        public EnumSet<SubFamily> getSubFamilies() {
            return this.subFamilies;
        }
    }
    
    public enum SubFamily {
        PLASMA_TV("プラズマ")
        ,LCD_TV("液晶")
        ,BLURAY_RECORDER("Blu-ray レコーダー")
        ,DVD_RECORDER("DVD レコーダー")
        ,NOISE_CANCELING_HEADPHONE("ノイズキャンセリング ヘッドホン")
        ,AUDIO_HEADPHONE("オーディオ ヘッドホン")
        ,MOBILE_HEADPHONE("モバイル ヘッドホン")
        ,SINGLE_LENS_DIGITAL_CAMERA("一眼レフ デジタルカメラ")
        ,COMPACT_DIGITAL_CAMERA("コンパクト デジタルカメラ")
        ,HDMI_CABLE("HDMI ケーブル")
        ,DVI_CABLE("DVI ケーブル")
        ,AV_CABLE("AV ケーブル");
        private String name;
        private SubFamily(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }
}
