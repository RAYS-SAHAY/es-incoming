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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for a2ACreditReplyDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="a2ACreditReplyDTO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ws.webgate.bpc.ru/}a2AReplyDTO"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="refnum" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "a2ACreditReplyDTO", propOrder = {
    "refnum"
})
public class A2ACreditReplyDTO
    extends A2AReplyDTO
{

    @XmlElement(required = true)
    protected String refnum;

    /**
     * Gets the value of the refnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefnum() {
        return refnum;
    }

    /**
     * Sets the value of the refnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefnum(String value) {
        this.refnum = value;
    }

}
