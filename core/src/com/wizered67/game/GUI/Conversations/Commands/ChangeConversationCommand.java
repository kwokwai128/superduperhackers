package com.wizered67.game.GUI.Conversations.Commands;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.GUI.Conversations.CompleteEvent;
import com.wizered67.game.GUI.Conversations.ConversationController;

/**
 * Created by Adam on 1/21/2017.
 */
public class ChangeConversationCommand implements ConversationCommand {
    private String newConversation;
    public ChangeConversationCommand(String conv) {
        newConversation = conv;
    }

    @Override
    public void execute(ConversationController conversationController) {
        conversationController.loadConversation(newConversation);
        conversationController.setBranch("default");
    }

    @Override
    public boolean waitToProceed() {
        return false;
    }

    @Override
    public void complete(CompleteEvent c) {

    }

    public static ChangeConversationCommand makeCommand(XmlReader.Element element) {
        String newConv = element.getAttribute("conv", null);
        if (newConv == null) {
            throw new GdxRuntimeException("No new conversation specified for new conversation command.");
        }
        return new ChangeConversationCommand(newConv);
    }

    @Override
    public void writeXml(XmlWriter xmlWriter) {

    }
}
