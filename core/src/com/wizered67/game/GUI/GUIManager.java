package com.wizered67.game.GUI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.wizered67.game.Constants;
import com.wizered67.game.Evidence;
import com.wizered67.game.GUI.Conversations.ConversationController;
import com.wizered67.game.GameManager;

/** Contains GUI elements and the ConversationController which the GUI elements are passed into.
 * Fixes GUI elements if screen is resized.
 * @author Adam Victor
 */
public class GUIManager {
    /** Main Table that all GUI elements are added to. */
	private static Table table;
    /** Skin used by all GUI elements. */
	private static Skin skin = new Skin();
    /** Label for the main textbox. Displays text when spoken by characters. */
	private static Label textboxLabel;
    /** Label to display the name of the current speaker. */
    private static Label speakerLabel;
    /** Array containing TextButtons to be displayed when the player is offered a choice. */
	private static TextButton[] choiceButtons;
    /** The stage to which the GUI elements are added. Part of Scene2D. */
	private static Stage stage;
    /** Constant Vector2 containing textbox dimensions. */
	private final static Vector2 TEXTBOX_SIZE = new Vector2(360, 50);
    /** Constant Vector2 containing choice button dimensions. */
    private final static Vector2 CHOICES_SIZE = new Vector2(300, 22);
    /** Constant denoting space between left side of the textbox and text. */
    private final static int LEFT_PADDING = 10;
    /** Message Window that updates the GUI elements as a Conversation proceeds. */
    private static ConversationController conversationController;
    static Table column;
    static Table main;
    static int listTypeIndex = 0;
    static List<String> list;
    static Array<Evidence>[] allEvidence = new Array[2];
    static Button presentButton;
    static Evidence currentEvidence;
    static TextButton backButton;
    static TextButton evidenceButton;
    static Label descriptionLabel;
    static LinkLabel infoLabel;
    static Sound clickSound;
    /** Initializes all of the GUI elements and adds them to the Stage ST. Also
     * initializes ConversationController with the elements it will update.
     */
    public GUIManager(Stage st){
		stage = st;
		clickSound = GameManager.assetManager().get("click");
 		// Generate a 1x1 white texture and store it in the skin named "white".
 		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
 		pixmap.setColor(Color.WHITE);
 		pixmap.fill();
 		skin.add("white", new Texture(pixmap));
 		// Store the default libgdx font under the name "default".
		BitmapFont defaultFont;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        float densityIndependentSize = Constants.REGULAR_FONT_SIZE * Gdx.graphics.getDensity();
        parameter.size = Math.round(densityIndependentSize);
		defaultFont = generator.generateFont(parameter); // font size 12 pixels
        defaultFont.getData().markupEnabled = true;
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
 		skin.add("default", defaultFont);
    	 table = new Table();
    	 table.setFillParent(true);
    	 stage.addActor(table);
	     //table.setDebug(true); // This is optional, but enables debug lines for tables.
    	    // Add widgets to the table here.
	     
	     TextButtonStyle textButtonStyle = new TextButtonStyle();
			textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.font = skin.getFont("default");
			skin.add("default", textButtonStyle);

        choiceButtons = new TextButton[4];
        for (int i = 0; i < choiceButtons.length; i += 1) {
            TextButton tb = new TextButton("", skin);
            tb.setUserObject(i);
            tb.addListener(new ChangeListener() {
                public void changed (ChangeEvent event, Actor actor) {
                    System.out.println("Clicked button " + actor.getUserObject());
                    conversationController.processChoice((Integer) actor.getUserObject());
                    event.cancel();
                }
            });
            tb.setVisible(false);
            choiceButtons[i] = tb;
            stage.addActor(tb);
        }

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = skin.getFont("default");
		Drawable newDrawable = skin.newDrawable("white", Color.DARK_GRAY);
		newDrawable.setLeftWidth(LEFT_PADDING);
        newDrawable.setLeftWidth(LEFT_PADDING);
		//newDrawable.setRightWidth(20);
		labelStyle.background = newDrawable;
		skin.add("default", labelStyle);
		textboxLabel = new Label("", skin);
		textboxLabel.setAlignment(Align.topLeft);
		textboxLabel.setStyle(labelStyle);
        textboxLabel.setWrap(true);
        stage.addActor(textboxLabel);

        Label.LabelStyle speakerLabelStyle = new Label.LabelStyle();
        speakerLabelStyle.font = skin.getFont("default");
        Drawable speakerDrawable = skin.newDrawable("white", Color.GRAY);
        speakerDrawable.setLeftWidth(5);
        //speakerDrawable.setRightWidth(5);
        //newDrawable.setRightWidth(20);
        speakerLabelStyle.background = speakerDrawable;
        skin.add("speaker", speakerLabelStyle);
        speakerLabel = new Label("Really long speaker name", skin, "speaker");
        //speakerLabel.setStyle(speakerLabelStyle);
        speakerLabel.setAlignment(Align.center);
        stage.addActor(speakerLabel);

        conversationController = new ConversationController(textboxLabel, speakerLabel, choiceButtons);
        setTextboxShowing(false);
        //System.out.println(remainingTextNoTags);
        //remainingText = "this is a new message just so you know.";
		//textboxLabel.setText("TESTING A MESSAGE BRO");
		//textboxLabel = new Label("this is a really long test message and I want to see if word wrap is doing anything? Test MessageCommand!", labelStyle);

		//textboxLabel.setPosition(200, 400);
		//textboxLabel.setSize(350, 60);
		//textboxLabel.setVisible(false);
		/*
        TextTooltip.TextTooltipStyle textTooltipStyle = new TextTooltip.TextTooltipStyle();
		textTooltipStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
		textTooltipStyle.label = labelStyle;
		textTooltipStyle.wrapWidth = 300;
		skin.add("default", textTooltipStyle);
		TextTooltip textTooltip = new TextTooltip("This [GREEN]is [WHITE]a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip!", skin);
		//textTooltip.setAlways(true);
		textTooltip.setInstant(true);
		button.addListener(textTooltip);
		*/
		//Table tooltipTable = new Table(skin);
		//tooltipTable.pad(10).background("default-round");
		//tooltipTable.add(new TextButton("Fancy tooltip!", skin));
        //evidenceTable = table.add().pad(100, 100, 100, 100)
        evidenceButton = new TextButton("Evidence", skin);
        evidenceButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                showEvidence(false);
                clickSound.play(2);
            }
        });
        evidenceButton.setX(50);
        evidenceButton.setY(Gdx.graphics.getHeight() - 50);
        evidenceButton.setWidth(100);
        evidenceButton.setHeight(40);
        stage.addActor(evidenceButton);

        initEvidence();
        column = new Table(skin);
        main = new Table(skin);
        table.top().left().add(column).minWidth(400);
        table.add(main).top().minWidth(400);
        List.ListStyle style = new List.ListStyle(defaultFont, Color.WHITE, Color.GRAY, skin.newDrawable("white", Color.GRAY));
        style.background = skin.newDrawable("white", Color.DARK_GRAY);
        list = new List<>(style);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Object o = list.getItems().get(list.getSelectedIndex());
                currentEvidence = (Evidence) o;
                if (descriptionLabel != null) {
                    descriptionLabel.setText("\n" + currentEvidence.description);
                    descriptionLabel.setHeight(descriptionLabel.getPrefHeight());
                    descriptionLabel.setY(545 - descriptionLabel.getHeight());
                    descriptionLabel.invalidate();
                }
                if (infoLabel != null) {
                    infoLabel.setUrl(currentEvidence.info);
                }
            }
        });
        updateList();
        //String[] type = new String[] {"BoR", "Rulings"};
        ImageButton.ImageButtonStyle[] imageButtonStyle = new ImageButton.ImageButtonStyle[2];
        Drawable d = new TextureRegionDrawable(new TextureRegion(GameManager.assetManager().get("Textures/amendmenticon.png", Texture.class)));
        imageButtonStyle[0] = new ImageButton.ImageButtonStyle(d, d, d, d, d, d);
        Drawable d2 = new TextureRegionDrawable(new TextureRegion(GameManager.assetManager().get("Textures/courtcaseicon.png", Texture.class)));
        imageButtonStyle[1] = new ImageButton.ImageButtonStyle(d2, d2, d2, d2, d2, d2);
        for (int i = 0; i < 2; i += 1) {
            Button button = new ImageButton(imageButtonStyle[i]);
            button.setUserObject(i);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    listTypeIndex = (int) actor.getUserObject();
                    updateList();
                    event.cancel();
                    clickSound.play(2);
                }
            });
            column.left().add(button).width(64).height(64).pad(30, 10, 10, 10);
        }
        column.row();
        column.add(list).minWidth(120).pad(0, 20, 0, 20).colspan(2);
        column.row();
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideEvidence();
                event.cancel();
                clickSound.play(2);
                if (presentButton.isVisible()) {
                    present(-1);
                }
            }
        });
        column.add(backButton).minWidth(100).minHeight(40).pad(5, 0, 0, 0).colspan(3);

        presentButton = new TextButton("Present", textButtonStyle);
        presentButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                present(currentEvidence.id);
            }
        });
        main.add(presentButton).minWidth(150).minHeight(40).pad(10, 0, 0, 0).colspan(3);
        main.row();
        VisUI.load();
        infoLabel = new LinkLabel("More Info", currentEvidence.info);
        infoLabel.toFront();
        main.add(infoLabel).colspan(3).pad(0, 0, 10, 0);
        Label.LabelStyle descriptionStyle = new Label.LabelStyle(defaultFont, Color.WHITE);
        descriptionStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        descriptionLabel = new Label("", descriptionStyle);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);
        stage.addActor(descriptionLabel);//.left().colspan(1);
        descriptionLabel.setX(Gdx.graphics.getWidth() - 400);
        descriptionLabel.setWidth(390);
        descriptionLabel.setText("\n" + currentEvidence.description);
        descriptionLabel.invalidate();
        descriptionLabel.setHeight(descriptionLabel.getPrefHeight());
        descriptionLabel.setY(545 - descriptionLabel.getHeight());
        descriptionLabel.invalidate();
        descriptionLabel.toBack();
        descriptionLabel.setVisible(false);
        main.setVisible(false);
        column.setVisible(false);
        //main.row();
        //table.add();
        /*
        for (int j = 0; j < 5; j += 1) {
            for (int i = 0; i < 5; i += 1) {
                Button button = new TextButton("TEST", textButtonStyle);
                table.add(button).width(100).height(100).pad(10, 10, 10, 10);
            }
            table.row();
        }
        */
	}

	private static void initEvidence() {
        allEvidence[0] = new Array<>(true, 10);
        allEvidence[1] = new Array<>(true, 6);
        allEvidence[0].add(new Evidence("First Amendment",
                "The Federal government, as well as the states, may not infringe on the people’s rights to press, free speech, assembly, petition, and to practice any religion.", 2,
                "https://en.wikipedia.org/wiki/First_Amendment_to_the_United_States_Constitution" ));

        allEvidence[0].add(new Evidence("Second Amendment",
                "People have the right to keep a weapon and use it to protect themselves.", 0,
                "https://en.wikipedia.org/wiki/Second_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Third Amendment", "Soldiers can not stay in people’s houses' without their consent.", 9, "https://en.wikipedia.org/wiki/Third_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Fourth Amendment",
                "The government cannot arrest a person, or search their property, unless there is \"probable cause\" that a crime has been committed.", 10, "https://en.wikipedia.org/wiki/Fourth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Fifth Amendment",
                "The Federal government must follow the due process of the law before punishing a person and that all citizens had the right to a trial by jury. It also states that a person cannot be put on trial twice for the same crime (the Double Jeopardy Clause) or that person on trial for a crime does not have to testify against themselves in court, known as \"Pleading the 5th\".",
                9, "https://en.wikipedia.org/wiki/Fifth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Sixth Amendment", "A person has the right to be told what crimes they are charged with, have a speedy and fair trial by a jury, to have a lawyer during the trial and the right to question witnesses against them and have the right to get their own witnesses to testify for them.", 9, "https://en.wikipedia.org/wiki/Sixth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Seventh Amendment", "People who are sued have to a jury trial for civil cases. ", 9, "https://en.wikipedia.org/wiki/Seventh_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Eighth Amendment", "The government cannot demand excessive bail, excessive fines, or inflict any cruel and unusual punishment.", 1, "https://en.wikipedia.org/wiki/Eighth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Ninth Amendment", "The Constitution does not include all of the rights of the people and the states. It provides reassurance that rights not listed could not be taken away and that the adoption of Constitution itself, and its construction, would limit the powers of government.", 9, "https://en.wikipedia.org/wiki/Ninth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Tenth Amendment", "Any powers that the Constitution does not give to the government, belong to the states and  the people, excluding the powers that the Constitution says the states cannot have.", 9, "https://en.wikipedia.org/wiki/Tenth_Amendment_to_the_United_States_Constitution"));

        allEvidence[1].add(new Evidence("New York Times v. Sullivan", "The Court held that the First Amendment protects the publication of all statements, even false ones, about the conduct of public officials except when statements are made with actual malice (with knowledge that they are false or in reckless disregard of their truth or falsity).", 9, "https://www.oyez.org/cases/1963/39"));
        allEvidence[1].add(new Evidence("UC Regents v. Bakke", "Race can be used as part of the criteria for admissions, but the use of quotas is unconstitutional.", 4, "https://www.oyez.org/cases/1979/76-811"));
        allEvidence[1].add(new Evidence("Roe v. Wade", "The Court held that a woman's right to an abortion fell within the right to privacy protected by the Fourteenth Amendment. The decision gave a woman total autonomy over the pregnancy during the first trimester and defined different levels of state interest for the second and third trimesters. ", 9, "https://www.oyez.org/cases/1971/70-18"));
        allEvidence[1].add(new Evidence("Texas v. Johnson", "Johnson's burning of a flag was protected expression under the First Amendment. The Court found that Johnson's actions fell into the category of expressive conduct and had a distinctively political nature. The fact that an audience takes offense to certain ideas or expression, the Court found, does not justify prohibitions of speech. ", 3, "https://www.oyez.org/cases/1988/88-155"));
        allEvidence[1].add(new Evidence("United States v. Santana", "The Court upheld the search. Relying on the the Court's decision in United States v. Watson (1976), Justice Rehnquist argued that by standing on her porch when the officers arrived, Santana was \"not in an area where she had any expectation of privacy.\" Since the police had probable cause to arrest and search her at that point, their behavior was consistent with the Court's Watson precedent.", 11, "https://www.oyez.org/cases/1975/75-19"));
        allEvidence[1].add(new Evidence("Mapp v. Ohio", "The Court declared that \"all evidence obtained by searches and seizures in violation of the Constitution is, by [the Fourth Amendment], inadmissible in a state court.\" Mapp had been convicted on the basis of illegally obtained evidence. This was an historic -- and controversial -- decision. It placed the requirement of excluding illegally obtained evidence from court at all levels of the government. ", 9, "https://www.oyez.org/cases/1960/236"));
        //allEvidence[1].add(new Evidence("New York Times Co. v Sullivan", "Hello world", 3, "https://en.wikipedia.org/wiki/New_York_Times_Co._v._Sullivan"));
        //allEvidence[1].add(new Evidence("Roe v Wade", "desc", 1, "https://en.wikipedia.org/wiki/Roe_v._Wade"));
    }

	private static void hideEvidence() {
        main.setVisible(false);
        column.setVisible(false);
        evidenceButton.setVisible(true);
        descriptionLabel.setVisible(false);
        infoLabel.setVisible(false);
    }

    public static void showEvidence(boolean canChoose) {
        main.setVisible(true);
        column.setVisible(true);
        evidenceButton.setVisible(false);
        presentButton.setVisible(canChoose);
        backButton.setVisible(true);
        //backButton.setVisible(!canChoose);
        descriptionLabel.setVisible(true);
        infoLabel.setVisible(true);
    }

    private static void updateList() {
        list.setItems(allEvidence[listTypeIndex]);
    }

    private static void present(int id) {
        hideEvidence();
        conversationController.present(id);
    }

    public GUIManager() {
        conversationController = new ConversationController();
    }
    /** Returns the Stage used to display GUI elements. */
	public static Stage getStage(){
		return stage;
	}
    /** Called every frame. Updates the ConversationController. DELTA TIME is the time
     * elapsed since the last frame.
     */
	public static void update(float deltaTime){
        conversationController.update(deltaTime);
        evidenceButton.setVisible(conversationController.isTextShowing() &&
                conversationController.doneSpeaking() && !main.isVisible());
	}

	public static boolean viewingEvidence() {
	    return main.isVisible();
    }

    /** Resize all GUI elements when the screen is resized to dimensions
     * WIDTH by HEIGHT. Keeps GUI elements proportional to virtual size.
     */
	public static void resize(int width, int height){
		textboxLabel.setSize(TEXTBOX_SIZE.x * width / Constants.VIRTUAL_WIDTH,
				TEXTBOX_SIZE.y * height / Constants.VIRTUAL_HEIGHT);
		textboxLabel.setPosition((width - textboxLabel.getWidth()) / 2,
				height / 16); //actor position is from bottom left of it
        textboxLabel.invalidate();
        //numLines = textboxLabel.getGlyphLayout().runs.size;
        speakerLabel.setPosition(textboxLabel.getX(), textboxLabel.getY() + textboxLabel.getHeight());
        speakerLabel.invalidate();

        resizeChoices(width, height);
	}
    /** Resizes the TextButtons used for choices. Keeps them evenly spaced apart. */
    private static void resizeChoices(int width, int height) {
        for (int i = 0; i < choiceButtons.length; i += 1) {
            TextButton tb = choiceButtons[i];
            tb.setSize(CHOICES_SIZE.x * width / Constants.VIRTUAL_WIDTH, CHOICES_SIZE.y * height / Constants.VIRTUAL_HEIGHT);
            float textBoxTop = textboxLabel.getTop();
            float gap = (height - textBoxTop - choiceButtons.length * tb.getHeight()) / (choiceButtons.length + 1);
            float yPos = textBoxTop + (gap + tb.getHeight() / 2) * ((choiceButtons.length - i - 1) + 1);
            tb.setPosition((width - tb.getWidth()) / 2, yPos);
            tb.invalidate();
        }
    }
    /** Returns the y position of the top of the textbox label. */
    public static float getTextboxTop() {
        return textboxLabel.getTop();
    }
    /** Sets the visibility of the textbox and speaker label to SHOW. */
    public static void setTextboxShowing(boolean show) {
        conversationController.setTextBoxShowing(show);
    }
    /** Returns the ConversationController. */
    public static ConversationController conversationController() {
        return conversationController;
    }
}
