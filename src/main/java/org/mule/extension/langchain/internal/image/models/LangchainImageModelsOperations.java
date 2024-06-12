package org.mule.extension.langchain.internal.image.models;

import org.mule.extension.langchain.internal.llm.LangchainLLMConfiguration;
import org.mule.extension.langchain.internal.llm.LangchainLLMParameters;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Config;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import software.amazon.awssdk.regions.Region;

import java.net.URI;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import static java.time.Duration.ofSeconds;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class LangchainImageModelsOperations {


	private ChatLanguageModel createModel(LangchainLLMConfiguration configuration, LangchainLLMParameters LangchainParams) {
	    ChatLanguageModel model = null;
	    switch (configuration.getLlmType()) {
	        case "OPENAI_API_KEY":
	  	      //System.out.println(configuration.getLlmType() + " - " + configuration.getLlmApiKey()); 
	            model = OpenAiChatModel.builder()
	                    .apiKey(configuration.getLlmApiKey()) 
	                    .modelName(LangchainParams.getModelName())
	                    .maxTokens(500)
	                    .build();
	            break;
	        case "MISTRALAI_API_KEY":
	            model = MistralAiChatModel.builder()
	                    .apiKey(configuration.getLlmApiKey())
	                    .modelName(LangchainParams.getModelName())
	                    .temperature(0.3)
	                    .timeout(ofSeconds(60))
	                    .logRequests(true)
	                    .logResponses(true)
	                    .build();
	            break;
	        case "OLLAMA_BASE_URL":
	            model = OllamaChatModel.builder()
	                    .baseUrl(configuration.getLlmApiKey())
	                    .modelName(LangchainParams.getModelName())
	                    .build();
	            break;
	        case "ANTHROPIC_API_KEY":
	            model = AnthropicChatModel.builder()
	                    .apiKey(configuration.getLlmApiKey())
	                    .modelName(LangchainParams.getModelName())
	                    .logRequests(true)
	                    .logResponses(true)
	                    .build();
	            break;
			case "AWS_BEDROCK_ID_AND_SECRET":
				//String[] creds = configuration.getLlmApiKey().split("mulechain"); 
				// For authentication, set the following environment variables:
        		// AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY
 				model = BedrockAnthropicMessageChatModel.builder()
						.region(Region.US_EAST_1)
						.temperature(0.30f)
						.maxTokens(300)
						.model(LangchainParams.getModelName())
						.maxRetries(1)
						.build();
				break;
	        default:
	            throw new IllegalArgumentException("Unsupported LLM type: " + configuration.getLlmType());
	    }
	    return model;
	}

	

	
	

	
	

	  /**
	   * Reads an image from an URL. 
	   */
	  @MediaType(value = ANY, strict = false)
	  @Alias("IMAGE-read")  
	  public String readFromImage(String data, String contextURL, @Config LangchainLLMConfiguration configuration, @ParameterGroup(name= "Additional properties") LangchainLLMParameters LangchainParams) {
	  
	      ChatLanguageModel model = createModel(configuration, LangchainParams);

          UserMessage userMessage = UserMessage.from(
                  TextContent.from(data),
                  ImageContent.from(contextURL)
          );

          Response<AiMessage> response = model.generate(userMessage);

          return response.content().text();
	  }  
  

	  /**
	   * Generates an image based on the prompt in data
	   */
	  @MediaType(value = ANY, strict = false)
	  @Alias("IMAGE-generate")  
	  public URI drawImage(String data, @Config LangchainLLMConfiguration configuration, @ParameterGroup(name= "Additional properties") LangchainLLMParameters LangchainParams) {
	  
          ImageModel model = OpenAiImageModel.builder()
                  .modelName(LangchainParams.getModelName())
                  .apiKey(configuration.getLlmApiKey())
                  .build();

          Response<Image> response = model.generate(data);
          System.out.println(response.content().url());
          return response.content().url();
	  }  
  

	  
	  
}