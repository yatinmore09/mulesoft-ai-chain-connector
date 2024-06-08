package org.mule.extension.langchain.internal.streaming;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import dev.langchain4j.service.TokenStream;

public class TokenStreamOutputResolver implements OutputTypeResolver<String> {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, String key) {
        //return metadataContext.getTypeLoader().load(TokenStream.class);
        return metadataContext.getTypeLoader().load(String.class);
    }

    @Override
    public String getCategoryName() {
        return "LangchainLLMPayload";
    }
}
