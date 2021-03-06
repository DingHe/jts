/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.testbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.locationtech.jtstest.testbuilder.model.Layer;
import org.locationtech.jtstest.testbuilder.ui.ColorUtil;
import org.locationtech.jtstest.testbuilder.ui.SwingUtil;
import org.locationtech.jtstest.testbuilder.ui.style.BasicStyle;
import org.locationtech.jtstest.testbuilder.ui.style.LayerStyle;

public class LayerStylePanel extends JPanel {
  private Layer layer;
  private JLabel title;
  private JPanel stylePanel;
  private int rowIndex;
  private JCheckBox cbDashed;
  private JSpinner spinnerLineWidth;
  private SpinnerNumberModel lineWidthModel;
  private JCheckBox cbFilled;
  private JSlider sliderFillAlpha;
  private JPanel btnFillColor;
  private JPanel btnLineColor;
  private JSlider sliderLineAlpha;
  
  private JCheckBox cbVertex;
  private JPanel btnVertexColor;
  private JSpinner spinnerVertexSize;
  private SpinnerNumberModel vertexSizeModel;
  
  private JCheckBox cbLabel;
  private JPanel btnLabelColor;
  private JSpinner spinnerLabelSize;
  private SpinnerNumberModel labelSizeModel;
  
  private JCheckBox cbStroked;
  private JTextField txtName;
  private JCheckBox cbOrient;
  private JCheckBox cbStructure;
  private JCheckBox cbVertexLabel;
  private JCheckBox cbOffset;
  private JSpinner spinnerOffsetSize;
  private SpinnerNumberModel offsetSizeModel;
  private JCheckBox cbEndpoint;

  
  public LayerStylePanel() {
    
    try {
      uiInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
  }
  private BasicStyle geomStyle() {
    return layer.getLayerStyle().getGeomStyle();
  }  
  public void setLayer(Layer layer, boolean isModifiable) {
    this.layer = layer;
    //this.title.setText("Styling - Layer " + layer.getName());
    txtName.setText(layer.getName());
    txtName.setEditable(isModifiable);
    txtName.setFocusable(isModifiable);

    cbVertex.setSelected(layer.getLayerStyle().isVertices());
    cbVertexLabel.setSelected(layer.getLayerStyle().isVertexLabels());
    vertexSizeModel.setValue(layer.getLayerStyle().getVertexSize());
    cbLabel.setSelected(layer.getLayerStyle().isLabel());
    labelSizeModel.setValue(layer.getLayerStyle().getLabelSize());
    cbEndpoint.setSelected(layer.getLayerStyle().isEndpoints());
    cbDashed.setSelected(geomStyle().isDashed());
    cbOffset.setSelected(layer.getLayerStyle().isOffset());
    offsetSizeModel.setValue( layer.getLayerStyle().getOffsetSize() );
    cbStroked.setSelected(geomStyle().isStroked());
    cbFilled.setSelected(geomStyle().isFilled());
    cbOrient.setSelected(layer.getLayerStyle().isOrientations());
    cbStructure.setSelected(layer.getLayerStyle().isOrientations());
    lineWidthModel.setValue(geomStyle().getStrokeWidth());
    updateStyleControls();
  }
  
  void updateStyleControls() {
    ColorControl.update(btnVertexColor, layer.getLayerStyle().getVertexColor() );
    ColorControl.update(btnLabelColor, layer.getLayerStyle().getLabelColor() );
    ColorControl.update(btnLineColor, geomStyle().getLineColor() );
    ColorControl.update(btnFillColor, geomStyle().getFillColor() );
    sliderLineAlpha.setValue(geomStyle().getLineAlpha());
    sliderFillAlpha.setValue(geomStyle().getFillAlpha());
    JTSTestBuilder.controller().updateLayerList();
  }
  
  private void uiInit() throws Exception {
    setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    setLayout(new BorderLayout());
     
     
    //title = new JLabel("Styling");
    //title.setAlignmentX(Component.LEFT_ALIGNMENT);
    //add(title, BorderLayout.NORTH);
    

    add( stylePanel(), BorderLayout.CENTER );
    
    JButton btnReset = SwingUtil.createButton(AppIcons.CLEAR, "Reset style to default", new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (layer == null) return;
        layer.resetStyle();
        updateStyleControls();
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    JPanel btnPanel = new JPanel();
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
    btnPanel.add(btnReset);
    add( btnPanel, BorderLayout.EAST);
  }
  
  private JPanel stylePanel() {
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
    
    stylePanel = new JPanel();
    stylePanel.setLayout(new GridBagLayout());
    stylePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    containerPanel.add(Box.createVerticalGlue());
    containerPanel.add(stylePanel);
    
    Dimension minSize = new Dimension(5, 100);
    Dimension prefSize = new Dimension(5, 100);
    Dimension maxSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    containerPanel.add(new Box.Filler(minSize, prefSize, maxSize));

    //=============================================
    txtName = new JTextField();
    txtName.setMaximumSize(new Dimension(100,20));
    txtName.setPreferredSize(new Dimension(100,20));
    txtName.setMinimumSize(new Dimension(100,20));
    addRow("Name", txtName);
    
    txtName.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        update();
      }
      public void removeUpdate(DocumentEvent e) {
        update();
      }
      public void insertUpdate(DocumentEvent e) {
        update();
      }

      public void update() {
        String name = txtName.getText();
        layer.setName(name);
        JTSTestBuilder.controller().updateLayerList();
      }
    });

    //=============================================

    cbVertex = new JCheckBox();
    cbVertex.setToolTipText(AppStrings.TIP_STYLE_VERTEX_ENABLE);
    cbVertex.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbVertex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setVertices(cbVertex.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    btnVertexColor = ColorControl.create(this, 
        "Vertex",
        AppColors.GEOM_VIEW_BACKGROUND,
        new ColorControl.ColorListener() {
          public void colorChanged(Color clr) {
            if (layer == null) return;
            layer.getLayerStyle().setVertexColor(clr);
            JTSTestBuilder.controller().geometryViewChanged();
          }
        }
       );
    
    vertexSizeModel = new SpinnerNumberModel(4.0, 0, 100.0, 1);
    spinnerVertexSize = new JSpinner(vertexSizeModel);
    spinnerVertexSize.setMaximumSize(new Dimension(40,16));
    spinnerVertexSize.setAlignmentX(Component.LEFT_ALIGNMENT);
    spinnerVertexSize.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int size = vertexSizeModel.getNumber().intValue();
        layer.getLayerStyle().setVertexSize(size);
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    cbVertexLabel = new JCheckBox();
    cbVertexLabel.setToolTipText(AppStrings.TIP_STYLE_VERTEX_LABEL_ENABLE);
    cbVertexLabel.setText("Label");
    cbVertexLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbVertexLabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setVertexLabels(cbVertexLabel.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });

    
    addRow("Vertices", cbVertex, btnVertexColor, spinnerVertexSize, cbVertexLabel);
    //=============================================

    cbStroked = new JCheckBox();
    cbStroked.setToolTipText(AppStrings.TIP_STYLE_LINE_ENABLE);
    cbStroked.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbStroked.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomStyle().setStroked(cbStroked.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });

    btnLineColor = ColorControl.create(this, 
        "Line",
        AppColors.GEOM_VIEW_BACKGROUND,
        new ColorControl.ColorListener() {
          public void colorChanged(Color clr) {
            geomStyle().setLineColor(clr);
            layer.getLayerStyle().setColor(clr);
            JTSTestBuilder.controller().geometryViewChanged();
            JTSTestBuilder.controller().updateLayerList();
          }
        }
       );
    JButton btnVertexSynch = SwingUtil.createButton("^", "Synch Vertex Color", new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (layer == null) return;
        Color clr = ColorControl.getColor(btnLineColor);
        layer.getLayerStyle().setColor(clr);
        layer.getLayerStyle().setVertexColor(clr);
        updateStyleControls();
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });

    lineWidthModel = new SpinnerNumberModel(1.0, 0, 100.0, 0.2);
    spinnerLineWidth = new JSpinner(lineWidthModel);
    //widthSpinner.setMinimumSize(new Dimension(50,12));
    //widthSpinner.setPreferredSize(new Dimension(50,12));
    spinnerLineWidth.setMaximumSize(new Dimension(40,16));
    spinnerLineWidth.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    spinnerLineWidth.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        float width = lineWidthModel.getNumber().floatValue();
        geomStyle().setStrokeWidth(width);
        JTSTestBuilder.controller().geometryViewChanged();
        JTSTestBuilder.controller().updateLayerList();
      }
    });

    sliderLineAlpha = createOpacitySlider(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (! source.getValueIsAdjusting()) {
          int alpha = (int)source.getValue();
          geomStyle().setLineAlpha(alpha);
          JTSTestBuilder.controller().geometryViewChanged();
          JTSTestBuilder.controller().updateLayerList();
        }
      }
    });
    addRow("Line", cbStroked, btnLineColor, btnVertexSynch, spinnerLineWidth, sliderLineAlpha);

    //=============================================
    cbDashed = new JCheckBox();
    cbDashed.setText("Dashed");
    //cbDashed.setToolTipText(AppStrings.STYLE_VERTEX_ENABLE);
    cbDashed.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbDashed.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        geomStyle().setDashed(cbDashed.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    
    cbEndpoint = new JCheckBox();
    cbEndpoint.setText("Endpoints");
    cbEndpoint.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbEndpoint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setEndpoints(cbEndpoint.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    
    cbOrient = new JCheckBox();
    cbOrient.setText("Orientation");
    cbOrient.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbOrient.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setOrientations(cbOrient.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });

    cbStructure = new JCheckBox();
    cbStructure.setText("Structure");
    cbStructure.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbStructure.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setStructure(cbStructure.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });

    cbOffset = new JCheckBox();
    cbOffset.setText("Offset");
    //cbDashed.setToolTipText(AppStrings.STYLE_VERTEX_ENABLE);
    cbOffset.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbOffset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setOffset(cbOffset.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    offsetSizeModel = new SpinnerNumberModel(LayerStyle.INIT_OFFSET_SIZE, -100, 100.0, 1);
    spinnerOffsetSize = new JSpinner(offsetSizeModel);
    spinnerOffsetSize.setMaximumSize(new Dimension(40,16));
    spinnerOffsetSize.setAlignmentX(Component.LEFT_ALIGNMENT);
    spinnerOffsetSize.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int size = offsetSizeModel.getNumber().intValue();
        layer.getLayerStyle().setOffsetSize(size);
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    
   // Leave on separate line to allow room for dash style
    addRow("", cbDashed, cbEndpoint, cbOrient, cbStructure, cbOffset, spinnerOffsetSize);
    //=============================================

    cbFilled = new JCheckBox();
    cbFilled.setToolTipText(AppStrings.TIP_STYLE_FILL_ENABLE);
    cbFilled.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbFilled.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomStyle().setFilled(cbFilled.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
        JTSTestBuilder.controller().updateLayerList();
      }
    });
   
    sliderFillAlpha = createOpacitySlider(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (! source.getValueIsAdjusting()) {
          int alpha = (int)source.getValue();
          geomStyle().setFillAlpha(alpha);
          JTSTestBuilder.controller().geometryViewChanged();
          JTSTestBuilder.controller().updateLayerList();
        }
      }
    });
    btnFillColor = ColorControl.create(this, 
        "Fill",
        AppColors.GEOM_VIEW_BACKGROUND,
        new ColorControl.ColorListener() {
          public void colorChanged(Color clr) {
            geomStyle().setFillColor(clr);
            updateStyleControls();
            JTSTestBuilder.controller().geometryViewChanged();
            JTSTestBuilder.controller().updateLayerList();
          }
        }
       );
    JButton btnLineSynch = SwingUtil.createButton("^", "Synch Line Color", new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Color clr = lineColorFromFill( ColorControl.getColor(btnFillColor));
        geomStyle().setLineColor(clr );
        layer.getLayerStyle().setColor(clr);
        updateStyleControls();
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    addRow("Fill", cbFilled, btnFillColor, btnLineSynch, sliderFillAlpha);

    //=============================================

    cbLabel = new JCheckBox();
    //cbLabel.setToolTipText(AppStrings.TIP_STYLE_VERTEX_ENABLE);
    cbLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbLabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (layer == null) return;
        layer.getLayerStyle().setLabel(cbLabel.isSelected());
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });
    btnLabelColor = ColorControl.create(this, 
        "Label",
        AppColors.GEOM_VIEW_BACKGROUND,
        new ColorControl.ColorListener() {
          public void colorChanged(Color clr) {
            if (layer == null) return;
            layer.getLayerStyle().setLabelColor(clr);
            JTSTestBuilder.controller().geometryViewChanged();
          }
        }
       );
    
    labelSizeModel = new SpinnerNumberModel(4.0, 0, 100.0, 1);
    spinnerLabelSize = new JSpinner(labelSizeModel);
    spinnerLabelSize.setMaximumSize(new Dimension(40,16));
    spinnerLabelSize.setAlignmentX(Component.LEFT_ALIGNMENT);
    spinnerLabelSize.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int size = labelSizeModel.getNumber().intValue();
        layer.getLayerStyle().setLabelSize(size);
        JTSTestBuilder.controller().geometryViewChanged();
      }
    });


    
    addRow("Label", cbLabel, btnLabelColor, spinnerLabelSize);
    
    //=============================================
    
    return containerPanel;
  }

  protected static Color lineColorFromFill(Color clr) {
    return ColorUtil.saturate(clr,  1);
    //return clr.darker();
  }

  private JSlider createOpacitySlider(ChangeListener changeListener) {
    JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, 255, 150);
    slide.addChangeListener(changeListener);
    slide.setMajorTickSpacing(32);
    slide.setPaintTicks(true);
    return slide;
  }

  private void addRow(String title, JComponent comp) {
    JLabel lbl = new JLabel(title);
    stylePanel.add(lbl, gbc(0, rowIndex, GridBagConstraints.EAST, 0.1));
    stylePanel.add(comp, gbc(1, rowIndex, GridBagConstraints.WEST, 1));
    rowIndex++;
  }

  /*
  private void xaddRow(String title, JComponent c1, JComponent c2) {
    addRow(title, c1, c2, null, null);
  }
  private void xaddRow(String title, JComponent c1, JComponent c2, JComponent c3) {
    addRow(title, c1, c2, c3, null, null);
  }
  private void xaddRow(String title, JComponent c1, JComponent c2, JComponent c3, JComponent c4) {
    addRow(title, c1, c2, c3, c4, null);
  }
*/
  
  private void addRow(String title, JComponent ... comp) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    for (JComponent c : comp) {
      panel.add(Box.createRigidArea(new Dimension(2,0)));
      panel.add(c);
    }
    addRow(title, panel);
  }
  
  private GridBagConstraints gbc(int x, int y, int align, double weightX) {
    return new GridBagConstraints(x, y, 
        1, 1, 
        weightX, 1, //weights
        align,
        GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2),
        2,
        0);
  }
}
