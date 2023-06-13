package com.example.demo.config;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.*;

public class CustomEndpointInterceptor extends EndpointInterceptorAdapter {
    private static final String SOAP_ENV_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelopes/";
    private static final String PREFERRED_PREFIX = "soap";

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
        SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
        alterSoapEnvelope(response);
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
        SaajSoapMessage soapResponse = (SaajSoapMessage) messageContext.getResponse();
        alterSoapEnvelope(soapResponse);
        return true;
    }


    private void alterSoapEnvelope(SaajSoapMessage soapResponse) {
        try {
            SOAPMessage soapMessage = soapResponse.getSaajMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPHeader header = soapMessage.getSOAPHeader();
            SOAPBody body = soapMessage.getSOAPBody();
            SOAPFault fault = body.getFault();
            envelope.removeNamespaceDeclaration(envelope.getPrefix());

            envelope.removeNamespaceDeclaration("SOAP-ENV");

            envelope.addNamespaceDeclaration(PREFERRED_PREFIX, SOAP_ENV_NAMESPACE);
            envelope.setPrefix(PREFERRED_PREFIX);
            header.setPrefix(PREFERRED_PREFIX);
            body.setPrefix(PREFERRED_PREFIX);
            if (fault != null) {
                fault.setPrefix(PREFERRED_PREFIX);
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }
}
