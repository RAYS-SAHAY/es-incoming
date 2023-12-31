//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.10.21 at 04:44:04 PM EAT 
//


package com.example.demo.object;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for airtelTopup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="airtelTopup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="EXTNWCODE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LOGINID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="PASSWORD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EXTCODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EXTREFNUM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MSISDN2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="LANGUAGE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LANGUAGE2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SELECTOR" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "airtelTopup", propOrder = {
    "type",
    "date",
    "extnwcode",
    "msisdn",
    "pin",
    "loginid",
    "password",
    "extcode",
    "extrefnum",
    "msisdn2",
    "amount",
    "language1",
    "language2",
    "selector"
})
public class AirtelTopup {

    @XmlElement(name = "TYPE", required = true)
    protected String type;
    @XmlElement(name = "DATE")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "EXTNWCODE", required = true)
    protected String extnwcode;
    @XmlElement(name = "MSISDN")
    protected String msisdn;
    @XmlElement(name = "PIN")
    protected String pin;
    @XmlElement(name = "LOGINID", required = true)
    protected String loginid;
    @XmlElement(name = "PASSWORD")
    protected String password;
    @XmlElement(name = "EXTCODE")
    protected String extcode;
    @XmlElement(name = "EXTREFNUM")
    protected String extrefnum;
    @XmlElement(name = "MSISDN2", required = true)
    protected String msisdn2;
    @XmlElement(name = "AMOUNT", required = true)
    protected String amount;
    @XmlElement(name = "LANGUAGE1", defaultValue = "0")
    protected String language1;
    @XmlElement(name = "LANGUAGE2", defaultValue = "0")
    protected String language2;
    @XmlElement(name = "SELECTOR", required = true, defaultValue = "1")
    protected String selector;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPE(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATE() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATE(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the extnwcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXTNWCODE() {
        return extnwcode;
    }

    /**
     * Sets the value of the extnwcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXTNWCODE(String value) {
        this.extnwcode = value;
    }

    /**
     * Gets the value of the msisdn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMSISDN() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMSISDN(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the pin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPIN() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPIN(String value) {
        this.pin = value;
    }

    /**
     * Gets the value of the loginid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOGINID() {
        return loginid;
    }

    /**
     * Sets the value of the loginid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOGINID(String value) {
        this.loginid = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPASSWORD() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPASSWORD(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the extcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXTCODE() {
        return extcode;
    }

    /**
     * Sets the value of the extcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXTCODE(String value) {
        this.extcode = value;
    }

    /**
     * Gets the value of the extrefnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXTREFNUM() {
        return extrefnum;
    }

    /**
     * Sets the value of the extrefnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXTREFNUM(String value) {
        this.extrefnum = value;
    }

    /**
     * Gets the value of the msisdn2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMSISDN2() {
        return msisdn2;
    }

    /**
     * Sets the value of the msisdn2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMSISDN2(String value) {
        this.msisdn2 = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAMOUNT() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAMOUNT(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the language1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLANGUAGE1() {
        return language1;
    }

    /**
     * Sets the value of the language1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLANGUAGE1(String value) {
        this.language1 = value;
    }

    /**
     * Gets the value of the language2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLANGUAGE2() {
        return language2;
    }

    /**
     * Sets the value of the language2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLANGUAGE2(String value) {
        this.language2 = value;
    }

    /**
     * Gets the value of the selector property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSELECTOR() {
        return selector;
    }

    /**
     * Sets the value of the selector property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSELECTOR(String value) {
        this.selector = value;
    }

}
