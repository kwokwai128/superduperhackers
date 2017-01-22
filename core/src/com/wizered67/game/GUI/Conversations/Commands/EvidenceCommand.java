package com.wizered67.game.GUI.Conversations.Commands;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.GUI.Conversations.CompleteEvent;
import com.wizered67.game.GUI.Conversations.ConversationController;
import com.wizered67.game.GUI.GUIManager;

/**
 * Created by Adam on 1/21/2017.
 */
public class EvidenceCommand implements ConversationCommand {
    private String variable;
    private boolean done;
    private String language;

    public EvidenceCommand(String lang, String var) {
        language = lang;
        variable = var;
        done = false;
    }

    @Override
    public void execute(ConversationController conversationController) {
        GUIManager.showEvidence(true);
        done = false;
    }

    @Override
    public boolean waitToProceed() {
        return !done;
    }

    @Override
    public void complete(CompleteEvent c) {
        if (c.type == CompleteEvent.Type.PRESENT) {
            done = true;
            ConversationController.scriptManager(language).setValue(variable, (int) c.data);
        }
    }

    public static EvidenceCommand makeCommand(XmlReader.Element element) {
        String language = element.getAttribute("language");
        String variable = element.getAttribute("variable");
        return new EvidenceCommand(language, variable);
    }

    @Override
    public void writeXml(XmlWriter xmlWriter) {

    }
}
