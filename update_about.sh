#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/MainActivity.kt"

# We want to replace the showHelpDialog implementation
awk '
/if \(showHelpDialog\) \{/ {
    print "    if (showHelpDialog) {"
    print "        AlertDialog("
    print "            onDismissRequest = { showHelpDialog = false },"
    print "            title = { Text(\"About & Help\") },"
    print "            text = {"
    print "                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {"
    print "                    Text("
    print "                        \"Developer: zakeer-career\","
    print "                        style = MaterialTheme.typography.titleMedium,"
    print "                        fontWeight = FontWeight.Bold,"
    print "                        color = MaterialTheme.colorScheme.primary"
    print "                    )"
    print "                    Text("
    print "                        \"To install this app directly on your Android phone without ADB or a PC:\\n\\n\" +"
    print "                        \"1. Tap the \\047Install\\047 or \\047Download APK\\047 button at the top right of the AI Studio window.\\n\" +"
    print "                        \"2. Open your Android phone\\047s Downloads folder or browser downloads.\\n\" +"
    print "                        \"3. Tap the downloaded .apk file.\\n\" +"
    print "                        \"4. If prompted, allow \\047Install from unknown sources\\047 for your browser or file manager.\""
    print "                    )"
    print "                }"
    print "            },"
    print "            confirmButton = {"
    print "                TextButton(onClick = { showHelpDialog = false }) {"
    print "                    Text(\"Got it\")"
    print "                }"
    print "            }"
    print "        )"
    print "    }"
    
    # skip lines until the closing brace of if (showHelpDialog)
    in_block=1
    next
}
in_block {
    if (/^    \}/) {
        in_block=0
    }
    next
}
{ print }
' "$FILE" > temp.kt && mv temp.kt "$FILE"

