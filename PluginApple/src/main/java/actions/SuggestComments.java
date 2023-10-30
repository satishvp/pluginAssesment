package actions;

import com.google.common.net.MediaType;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellijllij.openapi.;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemOutputDispatcherFactory;
import com.intellij.openapi.project.Project;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



public class SuggestComments extends AnAction{

    private static final String OPEN_API_KEY = "xxxx";
    private static final String OPEN_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project proj = e.getProject();

        String comment = "Generated Comment:  ";
        if(proj == null){
            return;
        }

        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        if(editor == null){
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if(selectedText != null && !selectedText.isEmpty()){
            int start = editor.getSelectionModel().getSelectionStart();

            String aiGeneratedComment = generateComment(selectedText);
            WriteCommandAction.runWriteCommandAction(proj , () -> {
                editor.getDocument().insertString(start,  aiGeneratedComment + "\n");
            });

        }
    }


    private String generateComment(String codeSnippet){


        CloseableHttpClient client = HttpClients.createDefault();

        // Create a HttpPost object to the API endpoint
        HttpPost post = new HttpPost("https://api.openai.com/v1/chat/completions");


        try {
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + OPEN_API_KEY);


            String requestBody = "{\n  \"model\": \"gpt-3.5-turbo\",\n  \"messages\": [\n    {\n      \"role\": \"system\",\n      \"content\": \"your reply will set as  comment to a code snippet\"\n    },\n    {\n      \"role\": \"user\",\n      \"content\": \"Suggest a short comment for following code samplecode  \"\n    }\n  ],\n  \"temperature\": 1,\n  \"max_tokens\": 256,\n  \"top_p\": 1,\n  \"frequency_penalty\": 0,\n  \"presence_penalty\": 0\n}";

            String formatCode = codeSnippet.replace("\"" , "\\\"");

            String updatedRequestBody = requestBody.replace("samplecode" , formatCode);


            post.setEntity(new StringEntity(updatedRequestBody, "UTF-8"));


            CloseableHttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());


                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                JsonNode choicesArray = rootNode.get("choices");

                if (choicesArray.isArray() && choicesArray.size() > 0) {
                    JsonNode message = choicesArray.get(0).get("message");

                    if (message != null) {
                        String comment = message.get("content").asText();

                        return  comment;


                    }
                }
            } else {
                System.out.println("Request was not successful. Status code: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Something went wrong.";
    }


}
