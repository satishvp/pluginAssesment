# IntelliJ Plugin - AI Code Comment Generator

## Overview

The IntelliJ AI Code Comment Generator plugin enhances your coding experience by automatically suggesting comments for selected code snippets. This README provides information on how to set up and use the plugin.

## Installation

1. Download the source code , build and use the IntelliJ plugin JAR file.

2. Open IntelliJ IDEA.

3. Go to `File` > `Settings` > `Plugins`.

4. Click the gear icon and select `Install Plugin from Disk...`.

5. Choose the downloaded JAR file.

6. Restart IntelliJ IDEA to activate the plugin.

## Usage

1. After installation, open an IntelliJ IDEA project.

2. Select the code snippet for which you want to generate a comment.

3. Right-click on the selected code or press a  "Suggest Comment".

4. The plugin will generate a comment based on the selected code and insert it above the code.

## Configuration

To configure your OpenAI API key and other settings, you need to modify `SuggestComment.java` in the plugin source code. Locate the following lines:

```java
private static final String OPEN_API_KEY = "xx....xxx";
private static final String OPEN_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
```



## Approach

1. Our Goal is to generate comment for selected Code. So ICreated action in plugin.xml file 
2. Overiride method "actionPerformed". 
	This method does following task 
	1. Create instanse fo Editor
	2. Extract text from Selection 
	3. Pass the the text to method "generateComment" , in return get response  (comment) from ChatGPT
	4. The reponse is then added to line above the selected text.


## Improvements.

1.Save the Secret Key in Secret Manager  
2.Current snippet doesn't format/intent the comment.
3.Validations could be added , to detect any attacks on the server, for eg. Selected text could be malware. 
4.Needs unit test cases. 
5.Eliminate the hard coded AI URL (can create an Interface , so it will be easy to replace the ChatGPT in future with other service - Decoupling)




















