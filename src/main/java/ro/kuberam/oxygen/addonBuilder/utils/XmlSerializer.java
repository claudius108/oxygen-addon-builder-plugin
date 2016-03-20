package ro.kuberam.oxygen.addonBuilder.utils;

import java.io.StringWriter;   
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

@SuppressWarnings("serial")
public class XmlSerializer extends ExtensionFunctionDefinition {
    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vis", "my.custom.uri", "serialize-xml");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.SINGLE_NODE };
    }

    @Override
    public SequenceType getResultType(SequenceType[] sequenceTypes) {
        return SequenceType.SINGLE_STRING;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext ctx, Sequence[] secs) throws XPathException {
                StringWriter escr = new StringWriter();
                try {
                    if (secs.length == 0) {
                        throw new XPathException("Missing argument");
                    } else {
                        Serializer serializer = new Processor(ctx.getConfiguration()).newSerializer(escr);
                        serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
                        serializer.serializeXdmValue(XdmValue.wrap(secs[0]));
                    }
                    return new StringValue(escr.toString());
                } catch (SaxonApiException ex) {
                    throw new XPathException("Error when invoking serialize-xml()", ex);
                }
            }
        };
    }
}
