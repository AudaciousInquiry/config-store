/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.store;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.nhindirect.common.cert.Thumbprint;

@Entity
@Table(name = "anchor")
/**
 * The JPA Domain class
 */
public class Anchor {

    private String owner;
    private String thumbprint;
    private long certificateId;
    private byte[] certificateData;
    private long id;
    private Calendar createTime;
    private Calendar validStartDate;
    private Calendar validEndDate;
    private EntityStatus status;
    private boolean incoming;
    private boolean outgoing;

    /**
     * Get the value of owner.
     * 
     * @return the value of owner.
     */
    @Column(name = "owner")
    public String getOwner() {
        return owner;
    }

    /**
     * Set the value of owner.
     * 
     * @param owner
     *            The value of owner.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the value of thumbprint.
     * 
     * @return the value of thumbprint.
     */
    @Column(name = "thumbprint")
    public String getThumbprint() {
        return thumbprint;
    }

    /**
     * Set the value of thumbprint.
     * 
     * @param thumbprint
     *            The value of thumbprint.
     */
    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    /**
     * Get the value of certificateData.
     * 
     * @return the value of certificateData.
     */
    @Column(name = "certificateData", length=4096)
    @Lob
    public byte[] getData() {
        return certificateData;
    }

    /**
     * Set the value of certificateData.
     * 
     * @param data
     *            The value of certificateData.
     * @throws CertificateException
     */
    public void setData(byte[] data) throws CertificateException {
        certificateData = data;
        if (data == Certificate.NULL_CERT) {
            setThumbprint("");
        } else {
            loadCertFromData();
        }
    }

    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the value of createTime.
     * 
     * @return the value of createTime.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreateTime() {
        return createTime;
    }

    /**
     * Set the value of createTime.
     * 
     * @param timestamp
     *            The value of createTime.
     */
    public void setCreateTime(Calendar timestamp) {
        createTime = timestamp;
    }

    /**
     * Get the value of validStartDate.
     * 
     * @return the value of validStartDate.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidStartDate() {
        return validStartDate;
    }

    /**
     * Set the value of validStartDate.
     * 
     * @param validStartDate
     *            The value of validStartDate.
     */
    public void setValidStartDate(Calendar validStartDate) {
        this.validStartDate = validStartDate;
    }

    /**
     * Get the value of validEndDate.
     * 
     * @return the value of validEndDate.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidEndDate() {
        return validEndDate;
    }

    /**
     * Set the value of validEndDate.
     * 
     * @param validEndDate
     *            The value of validEndDate.
     */
    public void setValidEndDate(Calendar validEndDate) {
        this.validEndDate = validEndDate;
    }

    /**
     * Get the value of status.
     * 
     * @return the value of status.
     */
    @Enumerated
    public EntityStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status.
     * 
     * @param status
     *            The value of status.
     */
    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    /**
     * Get the value of incoming.
     * 
     * @return the value of incoming.
     */
    @Column(name = "forIncoming")
    public boolean isIncoming() {
        return incoming;
    }

    /**
     * Set the value of incoming.
     * 
     * @param incoming
     *            The value of incoming.
     */
    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    /**
     * Get the value of outgoing.
     * 
     * @return the value of outgoing.
     */
    @Column(name = "forOutgoing")
    public boolean isOutgoing() {
        return outgoing;
    }

    /**
     * Set the value of outgoing.
     * 
     * @param outgoing
     *            The value of outgoing.
     */
    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    /**
     * Get the value of certificateId.
     * 
     * @return the value of certificateId.
     */
    public long getCertificateId() {
        return certificateId;
    }

    /**
     * Set the value of certificateId.
     * 
     * @param certificateId
     *            The value of certificateId.
     */
    public void setCertificateId(long certificateId) {
        this.certificateId = certificateId;
    }

    private X509Certificate loadCertFromData() throws CertificateException {
        X509Certificate cert = null;
        try {
            validate();
            ByteArrayInputStream bais = new ByteArrayInputStream(certificateData);
            cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            setThumbprint(Thumbprint.toThumbprint(cert).toString());
            bais.close();
        } catch (Exception e) {
            setData(Certificate.NULL_CERT);
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return cert;
    }

    public X509Certificate toCertificate() throws CertificateException {
        X509Certificate cert = null;
        try {
            validate();
            ByteArrayInputStream bais = new ByteArrayInputStream(certificateData);
            cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            bais.close();
        } catch (Exception e) {
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return cert;
    }
    
    
    
    private boolean hasData() {
        return ((certificateData != null) && (!certificateData.equals(Certificate.NULL_CERT))) ? true : false;
    }

    /**
     * Validate the Anchor for the existance of data.
     * 
     * @throws CertificateException
     */
    public void validate() throws CertificateException {
        if (!hasData()) {
            throw new CertificateException("Invalid Certificate: no certificate data exists");
        }
    }

}
