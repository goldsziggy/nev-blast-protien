//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.27 at 07:46:38 PM CDT 
//


package BLAST;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "iterationIterNum",
    "iterationQueryID",
    "iterationQueryDef",
    "iterationQueryLen",
    "iterationHits",
    "iterationStat",
    "iterationMessage"
})
@XmlRootElement(name = "Iteration")
public class Iteration {

    @XmlElement(name = "Iteration_iter-num", required = true)
    protected String iterationIterNum;
    @XmlElement(name = "Iteration_query-ID")
    protected String iterationQueryID;
    @XmlElement(name = "Iteration_query-def")
    protected String iterationQueryDef;
    @XmlElement(name = "Iteration_query-len")
    protected String iterationQueryLen;
    @XmlElement(name = "Iteration_hits")
    protected IterationHits iterationHits;
    @XmlElement(name = "Iteration_stat")
    protected IterationStat iterationStat;
    @XmlElement(name = "Iteration_message")
    protected String iterationMessage;

    /**
     * Gets the value of the iterationIterNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationIterNum() {
        return iterationIterNum;
    }

    /**
     * Sets the value of the iterationIterNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationIterNum(String value) {
        this.iterationIterNum = value;
    }

    /**
     * Gets the value of the iterationQueryID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationQueryID() {
        return iterationQueryID;
    }

    /**
     * Sets the value of the iterationQueryID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationQueryID(String value) {
        this.iterationQueryID = value;
    }

    /**
     * Gets the value of the iterationQueryDef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationQueryDef() {
        return iterationQueryDef;
    }

    /**
     * Sets the value of the iterationQueryDef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationQueryDef(String value) {
        this.iterationQueryDef = value;
    }

    /**
     * Gets the value of the iterationQueryLen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationQueryLen() {
        return iterationQueryLen;
    }

    /**
     * Sets the value of the iterationQueryLen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationQueryLen(String value) {
        this.iterationQueryLen = value;
    }

    /**
     * Gets the value of the iterationHits property.
     * 
     * @return
     *     possible object is
     *     {@link IterationHits }
     *     
     */
    public IterationHits getIterationHits() {
        return iterationHits;
    }

    /**
     * Sets the value of the iterationHits property.
     * 
     * @param value
     *     allowed object is
     *     {@link IterationHits }
     *     
     */
    public void setIterationHits(IterationHits value) {
        this.iterationHits = value;
    }

    /**
     * Gets the value of the iterationStat property.
     * 
     * @return
     *     possible object is
     *     {@link IterationStat }
     *     
     */
    public IterationStat getIterationStat() {
        return iterationStat;
    }

    /**
     * Sets the value of the iterationStat property.
     * 
     * @param value
     *     allowed object is
     *     {@link IterationStat }
     *     
     */
    public void setIterationStat(IterationStat value) {
        this.iterationStat = value;
    }

    /**
     * Gets the value of the iterationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationMessage() {
        return iterationMessage;
    }

    /**
     * Sets the value of the iterationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationMessage(String value) {
        this.iterationMessage = value;
    }

}