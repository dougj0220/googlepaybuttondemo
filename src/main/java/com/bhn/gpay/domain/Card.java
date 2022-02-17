package com.bhn.gpay.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Card {
    @XmlElement
    private String type;
    @XmlElement
    private String number;
    @XmlElement
    private String expDate;
    @XmlElement
    private String cardValidationNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCardValidationNum() {
        return cardValidationNum;
    }

    public void setCardValidationNum(String cardValidationNum) {
        this.cardValidationNum = cardValidationNum;
    }
}
