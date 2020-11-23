

/*
 * GenericStudentView.java
 *
 * Created on May 19, 2011, 1:45:14 PM
 */

package org.jdesktop.wonderland.modules.isocial.generic.client;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.modules.isocial.client.ISocialManager;
import org.jdesktop.wonderland.modules.isocial.client.view.ResultListener;
import org.jdesktop.wonderland.modules.isocial.client.view.SheetView;
import org.jdesktop.wonderland.modules.isocial.client.view.annotation.View;
import org.jdesktop.wonderland.modules.isocial.common.model.Result;
import org.jdesktop.wonderland.modules.isocial.common.model.ResultDetails;
import org.jdesktop.wonderland.modules.isocial.common.model.Role;
import org.jdesktop.wonderland.modules.isocial.common.model.Sheet;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericAnswer;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericQuestion;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericResult;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericSheet;
import org.jdesktop.wonderland.modules.isocial.generic.common.MultipleChoiceQuestion;

/**
 * Alternative view for testing purposes
 * @author ryan
 */
//@View(value=GenericSheet.class, roles=Role.STUDENT)
public class GenericStudentView extends javax.swing.JPanel
    implements SheetView, PropertyChangeListener, ResultListener {

    /** Creates new form GenericStudentView */
    private ISocialManager manager;
    private Sheet sheet;
    private Role role;
    private HUDComponent hudComponent;
    private static final Logger logger = Logger.getLogger(GenericStudentView.class.getName());
    private final Map<String, JPanel> questionsToPanels;
    private StudentReviewPanel reviewPanel;
    private int amountOfCards = 0;
    private int currentCardIndex = 1;
    public GenericStudentView() {
        initComponents();
        questionsToPanels = new HashMap<String, JPanel>();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        cardPanel = new javax.swing.JPanel();

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        cardPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cardPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(backButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 299, Short.MAX_VALUE)
                        .addComponent(nextButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton)
                    .addComponent(nextButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if(nextButton.getText().equals("Submit")) {
            nextButton.setText("Next");
        }
        currentCardIndex -= 1;
        
        CardLayout cl = (CardLayout)cardPanel.getLayout();
        cl.previous(cardPanel);
                
        if(currentCardIndex <= 1) {
           backButton.setEnabled(false);
           nextButton.setText("Next");
           nextButton.setEnabled(true);
        }
    }//GEN-LAST:event_backButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        currentCardIndex += 1;
        if(nextButton.getText().equals("Submit")) {
            //I don't think the last argument is used, rather an object just needs
            //to be put there..not sure though.
            firePropertyChange("submit", null, getResultDetails());
            //nextButton.setEnabled(false);
            nextButton.setText("Next");
            currentCardIndex = 1;
            CardLayout cl = (CardLayout)cardPanel.getLayout();
            cl.first(cardPanel);
            return;
        }

        if(currentCardIndex > amountOfCards) {
            createReviewPanel();

            nextButton.setText("Submit");
            //nextButton.setEnabled(false);
            backButton.setEnabled(true);
        }
        CardLayout cl = (CardLayout)cardPanel.getLayout();
        cl.next(cardPanel);

    }//GEN-LAST:event_nextButtonActionPerformed

    public void initialize(ISocialManager ism, Sheet sheet, Role role) {
        this.manager = ism;
        this.sheet = sheet;
        this.role = role;

        this.addPropertyChangeListener(this);
        manager.addResultListener(sheet.getId(), this);

        //create panels here based on GenericSheet
        createPanels();

        try {
            for(Result r: manager.getResults(sheet.getId())) {
                if(r.getCreator().equals(manager.getUsername()))
                    setResult(r);
            }
        } catch(IOException ioe) {
            logger.warning("Error reading results...");
        }
    }

    public String getMenuName() {
        GenericSheet details = (GenericSheet)sheet.getDetails();
        return details.getName();
    }

    public boolean isAutoOpen() {
        GenericSheet details = (GenericSheet)sheet.getDetails();
        return details.isAutoOpen();
    }

    public HUDComponent open(HUD hud) {
        hudComponent = hud.createComponent(this);
        return hudComponent;
    }

    public void close() {
        manager.removeResultListener(sheet.getId(), this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("submit")) {

            //house keeping first. We need to change the button text to "next"
            //and re-enable it in case we ever open that window again.
            nextButton.setEnabled(true);
            nextButton.setText("Next");
            

            GenericResult details = (GenericResult)getResultDetails();
            try {
                Result r = manager.submitResult(sheet.getId(), details);
                setResult(r);
                hudComponent.setVisible(false);
            } catch(IOException e) {
                logger.warning("Error submitting result.");
                e.printStackTrace();
            }
        }
    }

    public void resultAdded(Result result) {

        if(!result.getCreator().equals(manager.getUsername())) {
            logger.warning("Ignoring result, it's not for us.");
            return;
        }
        if(result.getCreator().equals(manager.getUsername())) {
            setResult(result);
        }
         logger.warning("RESULT ADDED!");
        //asdf
    }

    public void resultUpdated(Result result) {
        //throw new UnsupportedOperationException("Not supported yet.");
        logger.warning("RESULT UPDATED!");
    }

    public void setResult(Result result) {
        if(result.getDetails() instanceof GenericResult) {
            GenericResult genericResult = (GenericResult)result.getDetails();
            List<GenericAnswer> answers = genericResult.getAnswers();

            //for every answer...
            for(GenericAnswer answer: answers) {
                //...get the panel associated with the question...
                MultipleChoiceQuestionPanel panel = (MultipleChoiceQuestionPanel)questionsToPanels.get(answer.getQuestionTitle());
                
                //...set the selected answer in its respective question's panel...
                panel.setSelectedAnswer(answer.getValue());

                       
            }
            //...next recreate the review panel...

                //...first get the layout...
                CardLayout layout = (CardLayout)cardPanel.getLayout();
                //...set the layout to the first card in case the review panel
                //is currently the active card.
                layout.first(cardPanel);
                
        }
        else {
            logger.warning("Cannot set result, result is not an instance of GenericResult!");
        }
    }

    public ResultDetails getResultDetails() {
        GenericResult details = new GenericResult();

        //for every panel in our map...
        for(JPanel panel: questionsToPanels.values()) {
            //...cast the panel...
            MultipleChoiceQuestionPanel mPanel = (MultipleChoiceQuestionPanel)panel;

            //...for each list of selected answers...
            for(String answer: mPanel.getSelectedAnswer()) {
                //...create the generic answer object...
                GenericAnswer gAnswer = new GenericAnswer();

                //...set the question value of the answer...
                gAnswer.setQuestionTitle(mPanel.getQuestion());

                //...set the value of the answer...
                gAnswer.setValue(answer);

                //...finally add the object to the result details.
                details.addAnswer(gAnswer);
            }
        }
        return details;
    }

    private void createPanels() {
        //always check
        if(sheet.getDetails() instanceof GenericSheet) {
            GenericSheet genericSheet = (GenericSheet)sheet.getDetails();
            for(GenericQuestion question: genericSheet.getQuestions()) {
                //only checking MultipleChoiceQuestions for right now...
                //this should change as new question types are added.
                if(cardPanel.getLayout() instanceof CardLayout && question instanceof MultipleChoiceQuestion) {
                    //add a new panel
                    //cast the question
                    MultipleChoiceQuestion mcQuestion = (MultipleChoiceQuestion)question;
                    //create the panel based on the question
                    MultipleChoiceQuestionPanel questionPanel =
                            new MultipleChoiceQuestionPanel(mcQuestion, mcQuestion.getInclusive());
                    //map the question to the panel
                    questionsToPanels.put(question.getValue(), questionPanel);
                    //add the panel to the list of cards
                    logger.warning("Adding card to panel!");
                    cardPanel.add(questionPanel,"");
                    amountOfCards += 1;
                }
                logger.warning("Unable to create cards. No card layout OR MultipleChoiceQuestion found");
            }
        } else {
            logger.warning("Not a GenericSheet. Unable to create panels");
        }
    }

    private void createReviewPanel() {
        if(reviewPanel != null) {
            CardLayout layout = (CardLayout)cardPanel.getLayout();
            layout.removeLayoutComponent(reviewPanel);
        }
        List<String> columns = new ArrayList<String>();
        List<String> answers = new ArrayList<String>();
        Map<String, List<String>> tableData = new HashMap<String, List<String>>();
        for(JPanel panel : questionsToPanels.values()) {
            if(panel instanceof MultipleChoiceQuestionPanel) {

                MultipleChoiceQuestionPanel questionPanel = (MultipleChoiceQuestionPanel)panel;
                tableData.put(questionPanel.getQuestion(),
                              questionPanel.getSelectedAnswer());

            } else {
                logger.warning("Refusing to process panel: Not a MultipleChoiceQuestionPanel!");
            }
        }
        reviewPanel = new StudentReviewPanel(tableData);
        cardPanel.add(reviewPanel, "review");

        //amountOfCards += 1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables

}
