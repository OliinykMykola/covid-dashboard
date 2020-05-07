package com.olejnik.nick.views.main;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.theme.Theme;import com.vaadin.flow.theme.lumo.Lumo;

@JsModule("./styles/shared-styles.js")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout {

}
