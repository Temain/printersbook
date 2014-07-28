package com.temain.printersbook;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import com.temain.model.Factory;
import com.temain.model.entities.Events;
import com.temain.model.entities.Podrs;
import com.temain.model.entities.Printers;
import com.temain.model.util.AuthenticateUtil;
import com.temain.model.util.HibernateUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;
import javafx.application.Application;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

@SuppressWarnings("serial")
@Theme("mytheme")
public class MainForm extends UI {
	
	@WebServlet(value = { "/*" ,"/VAADIN/*" }, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainForm.class)
	public static class Servlet extends VaadinServlet {
	}

	private static Logger log = Logger.getLogger(MainForm.class.getName()); 
	private AuthenticateUtil authUtil = new AuthenticateUtil();
	
	private FormLayout editorLayout = new FormLayout();
	private ComboBox cmbPodrs;
	private VerticalLayout eventsLayout = new VerticalLayout();
	private VerticalLayout podrsLayout = new VerticalLayout();
	private VerticalLayout helloLayout = new VerticalLayout();
    private FormLayout reportLayout = new FormLayout();
	private Table events = new Table();
	private Table printers = new Table();
	private Table podrsTable = new Table();
	private FieldGroup editorFields = new FieldGroup();
	private TabSheet tabSheet = new TabSheet();
	private TextField searchField = new TextField();
    private TextField fieldInvent = new TextField("Инвентарный номер:");
    private TextField fieldModel = new TextField("Модель:");
    private TextField fieldMatOtv = new TextField("Мат. ответственный:");
    private TextField fieldNumber = new TextField("Номер телефона:");
	
    private Button addNewPrinterButton = new Button("Добавить");
    private Button removePrinterButton = new Button("Удалить");
    private Button savePrinterButton = new Button("Сохранить");
    
    private Button addNewEventButton = new Button("Добавить");
    private Button removeEventButton = new Button("Удалить");
    private Button editEventButton = new Button("Редактировать");
    
    private Button addNewPodrButton = new Button("Добавить");
    private Button removePodrButton = new Button("Удалить");
    private Button editPodrButton = new Button("Редактировать");

    private PopupDateField startDate = new PopupDateField("C:");
    private PopupDateField endDate = new PopupDateField("По:");

    private Button reportButton = new Button("Показать отчет");
    private Button report2Button = new Button("Показать отчет");
    private Button report2ButtonXLS = new Button("В Excel");
    private ComboBox reportPodrs;
    
    private IndexedContainer container = createPrintersDataSource();   
    private IndexedContainer eventsContainer = createEventsDataSource();
    private IndexedContainer podrsContainer = createPodrsDataSource();
    private BeanItemContainer<Podrs> podrsContainerCmb;
    
	@Override
	protected void init(VaadinRequest request) {
		initLoginForm();
		//initLogger();
	}

	private void initUI(){
		initLayout();
		initPrintersTable();
		initEditor();
		initAddSaveRemoveButtons();
		initSearch();
		initEventsTable();
		initAddEditRemoveEventsButtons();
		initPodrsTable();
		initAddEditRemovePodrsButons();
        initReportLayout();
	}

	private void initLoginForm(){
		VerticalLayout loginLayout = new VerticalLayout();
		setContent(loginLayout);
		
		final Window loginWindow = new Window(" Вход");
		loginWindow.setIcon(new ThemeResource("pics/logotip.png"));		
		
		Label title = new Label("Пожалуйста, введите логин и пароль:");
		final TextField username = new TextField("Логин:");
		username.setWidth("300px");
		username.setRequired(true);
		username.setInvalidAllowed(false);
		
		final PasswordField password = new PasswordField("Пароль:");
		password.setWidth("300px");
		password.setRequired(true);
		password.setNullRepresentation("");
		
		
		final Button loginButton = new Button("Войти");
		loginButton.setIcon(new ThemeResource("pics/login3.png"));
		loginButton.setWidth("120px");
		
		loginButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {		
				if(authUtil.authenticate(username.getValue(), password.getValue())){
					Notification.show("Добро пожаловать, " + authUtil.getUserName() + "!", Type.TRAY_NOTIFICATION);
					initUI();
					loginWindow.close();
					removeWindow(loginWindow);
				}else if(username.getValue() == "" || password.getValue() == ""){
					Notification.show("Необходимо заполнить все поля.", Type.ERROR_MESSAGE);
				}else{
					Notification.show("Вы ввели неверный логин или пароль.", Type.ERROR_MESSAGE);
				}
			}
		});		
		
		//login button click on ENTER
		password.addFocusListener(new FocusListener() {
	        @Override
	        public void focus(FocusEvent event) {
	            loginButton.setClickShortcut(KeyCode.ENTER);
	        }
	    });
	    password.addBlurListener(new BlurListener() {
	        @Override
	        public void blur(BlurEvent event) {
	        	loginButton.removeClickShortcut();
	        }
	    });
	    
	    username.addFocusListener(new FocusListener() {
	        @Override
	        public void focus(FocusEvent event) {
	            loginButton.setClickShortcut(KeyCode.ENTER);
	        }
	    });
	    username.addBlurListener(new BlurListener() {
	        @Override
	        public void blur(BlurEvent event) {
	        	loginButton.removeClickShortcut();
	        }
	    });
		
		VerticalLayout fields = new VerticalLayout(title, username, password, loginButton);
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, true));
        fields.setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
        fields.setSizeUndefined();
        
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLACK);
        
        loginWindow.setContent(viewLayout);
		
        loginWindow.setClosable(false);
        loginWindow.setResizable(false);
        loginWindow.setDraggable(false);
		
        loginWindow.center();
        loginWindow.setModal(true);
		addWindow(loginWindow);
	}	
	
	private void initLayout(){	
		Page.getCurrent().setTitle("Учет использования принтеров");
		//------------------main layout-------------------------
		VerticalLayout mainLayout = new VerticalLayout();
		setContent(mainLayout);
		mainLayout.setSizeFull();
		
		//-------------------Header label-----------------------
		//Label top = new Label("Учет использования принтеров.");
		//top.addStyleName(Reindeer.LABEL_H1);
		
		//-------------------Middle panel-----------------------
	    final Panel middlePanel = new Panel();
	    middlePanel.addStyleName(Runo.PANEL_LIGHT);
	    middlePanel.setSizeFull();	    
	    
	    HorizontalLayout centerLayout = new HorizontalLayout();
	    centerLayout.setSizeFull();

	    //----------------------Split panel-----------------------
	    HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
	    splitPanel.setSizeFull();
	    
	    VerticalLayout leftSplitLayout = new VerticalLayout();
	    leftSplitLayout.setSizeFull();
	    leftSplitLayout.addComponent(printers);	    	    
	    leftSplitLayout.setExpandRatio(printers, 1);	    
 	    
	    helloLayout.setSizeFull();
	    helloLayout.setMargin(true);
	    helloLayout.setStyleName("backColor");
        Embedded image = new Embedded("",new ThemeResource("pics/logotip.jpg"));
        helloLayout.addComponent(image);
        helloLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER); 
        
        tabSheet.addStyleName(Reindeer.TABSHEET_BORDERLESS);
	    tabSheet.addTab(editorLayout, "Редактирование");
	    tabSheet.getTab(0).setIcon(new ThemeResource("pics/edit.ico"));
	    tabSheet.addTab(eventsLayout, "История принтера");
	    tabSheet.getTab(1).setIcon(new ThemeResource("pics/history.ico"));
	    tabSheet.addTab(podrsLayout, "Подразделения");
	    tabSheet.getTab(2).setIcon(new ThemeResource("pics/organisation.png"));
        tabSheet.addTab(reportLayout, "Отчеты");
        tabSheet.getTab(3).setIcon(new ThemeResource("pics/report.png"));
	    tabSheet.setSizeFull();	
	    tabSheet.setVisible(false);
	    
	    VerticalLayout rightSplitLayout = new VerticalLayout();
	    rightSplitLayout.setSizeFull();
	    rightSplitLayout.addComponent(helloLayout);
	    rightSplitLayout.addComponent(tabSheet);
	    
	    eventsLayout.addComponent(events);
	    eventsLayout.setExpandRatio(events, 1.0f);
	    eventsLayout.setSizeFull();
	    events.setSizeFull();
	    
	    podrsLayout.addComponent(podrsTable);
	    podrsLayout.setExpandRatio(podrsTable, 1.0f);
	    podrsLayout.setSizeFull();
	    	 
    	//search field and add button
	    HorizontalLayout bottomLeftSplitLayout = new HorizontalLayout();	    
	    bottomLeftSplitLayout.setWidth("100%");   
	    searchField.setWidth("100%");
	    bottomLeftSplitLayout.addComponent(searchField);
	    bottomLeftSplitLayout.setExpandRatio(searchField, 1.0f);
	    leftSplitLayout.addComponent(bottomLeftSplitLayout);
	    
	    if(authUtil.isUserInRole("ROLE_ADMIN")){ 	

		    addNewPrinterButton.setIcon(new ThemeResource("pics/add.png"));
		    addNewPrinterButton.setWidth("130px");	    
		    bottomLeftSplitLayout.addComponent(addNewPrinterButton);
		    
	    	//init events edit layout
		    HorizontalLayout bottomRightLayout = new HorizontalLayout();
		    
		    HorizontalLayout panel = new HorizontalLayout();
		    panel.setWidth("100%");
	
		    bottomRightLayout.addComponent(panel);
		    bottomRightLayout.addComponent(addNewEventButton);
		    bottomRightLayout.addComponent(editEventButton);
		    bottomRightLayout.addComponent(removeEventButton);
		    bottomRightLayout.setSpacing(true);
		    
		    addNewEventButton.setIcon(new ThemeResource("pics/add.png"));
		    removeEventButton.setIcon(new ThemeResource("pics/delete.png"));
		    editEventButton.setIcon(new ThemeResource("pics/edit.ico"));
		    addNewEventButton.setWidth("130px");
		    removeEventButton.setWidth("130px");
		    editEventButton.setWidth("130px");
		    
		    bottomRightLayout.setExpandRatio(panel, 1.0f);
		    bottomRightLayout.setWidth("100%");
		    
		    eventsLayout.addComponent(bottomRightLayout);
	    	    	    	    	    
		    //init podrs edit layout
		    HorizontalLayout bottomPodrsLayout = new HorizontalLayout();
		    
		    HorizontalLayout bottomPanel = new HorizontalLayout();
		    bottomPanel.setWidth("100%");
	
		    bottomPodrsLayout.addComponent(bottomPanel);
		    bottomPodrsLayout.addComponent(addNewPodrButton);
		    bottomPodrsLayout.addComponent(editPodrButton);
		    bottomPodrsLayout.addComponent(removePodrButton);
		    bottomPodrsLayout.setSpacing(true);
		    
		    addNewPodrButton.setIcon(new ThemeResource("pics/add.png"));
		    removePodrButton.setIcon(new ThemeResource("pics/delete.png"));
		    editPodrButton.setIcon(new ThemeResource("pics/edit.ico"));
		    addNewPodrButton.setWidth("130px");
		    removePodrButton.setWidth("130px");
		    editPodrButton.setWidth("130px");
		    
		    bottomPodrsLayout.setExpandRatio(bottomPanel, 1.0f);
		    bottomPodrsLayout.setWidth("100%");
		    
	        podrsLayout.setSizeFull();
		    podrsLayout.addComponent(bottomPodrsLayout);	 
	    }
	    
	    splitPanel.addComponent(leftSplitLayout);
	    splitPanel.addComponent(rightSplitLayout);	   
	    	 
	    centerLayout.addComponent(splitPanel);
	    middlePanel.setContent(centerLayout);

	    //-------------------Footer label-----------------------
	    //Label bottom = new Label("FOOTER");
	    
		//mainLayout.addComponent(top);
	    mainLayout.addComponent(middlePanel);
	    //mainLayout.addComponent(bottom);
	    mainLayout.setExpandRatio(middlePanel, 1.0f);	    
	}
		
	private void initEditor() {	
		//get data from db for podrs combobox
		List<Podrs> podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
		podrsContainerCmb = new BeanItemContainer<Podrs>(Podrs.class ,podrs);		
		cmbPodrs = new ComboBox("Подразделение:", podrsContainerCmb);
		cmbPodrs.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPodrs.setItemCaptionPropertyId("title");
		cmbPodrs.setNullSelectionAllowed(true);
		cmbPodrs.setInputPrompt("Выберите подразделение...");
		cmbPodrs.setRequired(true);
		cmbPodrs.setRequiredError("Необходимо указать подразделение.");
		cmbPodrs.setImmediate(true);
		cmbPodrs.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {	
				Podrs podr = (Podrs)event.getProperty().getValue();
				if(podr != null){
					fieldMatOtv.setValue(podr.getMatOtv());
					fieldNumber.setValue(podr.getTelefon());
				}
			}
		});
			
        fieldInvent.setInputPrompt("Введите инвентарный номер...");
        fieldInvent.setMaxLength(11);
        fieldModel.setInputPrompt("Введите модель принтера...");   
        fieldModel.setMaxLength(128);
        fieldModel.setRequired(true);
        fieldModel.setRequiredError("Необходимо указать модель принтера.");
        
        fieldModel.setImmediate(true);
        fieldModel.setValidationVisible(true);
        fieldMatOtv.setInputPrompt("Укажите мат. ответственного...");
        fieldMatOtv.setMaxLength(128);
        fieldNumber.setInputPrompt("Введите номер телефона...");
        fieldMatOtv.setMaxLength(128);
        fieldMatOtv.setNullSettingAllowed(true);
        fieldNumber.setNullSettingAllowed(true);
        
		cmbPodrs.setWidth("100%");
        fieldInvent.setWidth("100%");
        fieldModel.setWidth("100%");
        fieldMatOtv.setWidth("100%");
        fieldNumber.setWidth("100%");
        
        removePrinterButton.setIcon(new ThemeResource("pics/delete.png"));
        savePrinterButton.setIcon(new ThemeResource("pics/save.png"));
        
        editorFields.bind(fieldInvent, "invent");         
        editorFields.bind(fieldModel, "model");
        editorFields.setBuffered(false);
        
		editorLayout.addComponent(cmbPodrs);
        editorLayout.addComponent(fieldInvent);
        editorLayout.addComponent(fieldModel);
        editorLayout.addComponent(fieldMatOtv);
        editorLayout.addComponent(fieldNumber);
        
        if(authUtil.isUserInRole("ROLE_ADMIN")){
        	//save and remove buttons
	        HorizontalLayout buttonsLayout = new HorizontalLayout();
	        buttonsLayout.setWidth("250px");
	        buttonsLayout.setSpacing(true);
	        buttonsLayout.addComponent(removePrinterButton);  
	        buttonsLayout.addComponent(savePrinterButton);
	        buttonsLayout.setExpandRatio(removePrinterButton, 1.0f);
	        buttonsLayout.setExpandRatio(savePrinterButton, 1.0f);
	        removePrinterButton.setWidth("100%");
	        savePrinterButton.setWidth("100%");
	        
	        editorLayout.addComponent(buttonsLayout);
        }
        editorLayout.setVisible(false);
	    editorLayout.setMargin(true);
	}
	
	private void refillPodrCmb(){
		List<Podrs> podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
		podrsContainerCmb.removeAllItems(); //= new BeanItemContainer<Podrs>(Podrs.class ,podrs);	
		podrsContainerCmb.addAll(podrs);
		cmbPodrs.setContainerDataSource(podrsContainerCmb);
		Object printerObjectId = printers.getValue();//--------------------								
        if (printerObjectId != null){
        	Item item = printers.getItem(printerObjectId);	            	
		    editorFields.setItemDataSource(item);		
		    Object itemPodrId = item.getItemProperty("podrId").getValue();
		    Object itemPodrTitle = item.getItemProperty("podrTitle").getValue();			    
		    if(itemPodrId != null && itemPodrTitle != null){ //for add printer item
			    Podrs podr = new Podrs();
			    podr.setId(Integer.parseInt(itemPodrId.toString()));
			    podr.setTitle(itemPodrTitle.toString());
			    cmbPodrs.select(podr);
			}else{
			    cmbPodrs.setValue(null);
			}
        }
	}
	
	private void initPrintersTable(){
		printers.setContainerDataSource(container);		
		printers.setColumnHeader("invent", "Инвентарный номер");
		printers.setColumnHeader("podrTitle", "Подразделение");
		printers.setColumnHeader("model", "Модель");
		
		printers.setColumnIcon("invent", new ThemeResource("pics/number.png"));
		printers.setColumnIcon("podrTitle", new ThemeResource("pics/organisation.png"));
		printers.setColumnIcon("model", new ThemeResource("pics/model.png"));
		
		printers.setColumnExpandRatio("invent", 1.0f);
		printers.setColumnExpandRatio("podrTitle", 2.0f);
		printers.setColumnExpandRatio("model", 1.0f);
		
		printers.setVisibleColumns(new Object[] {"invent","podrTitle","model"});
		printers.setRowHeaderMode(RowHeaderMode.INDEX);
		
		printers.setImmediate(true);
		printers.setSelectable(true);	
		printers.setSizeFull();
		
		printers.setColumnWidth(null, 30);
		
		//set data for editform from printers table
		printers.addValueChangeListener(new ValueChangeListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {	
				
				//set icon for row header
				for (Iterator i = printers.getItemIds().iterator(); i.hasNext();) {
				    int iid = (Integer) i.next();
					printers.setItemIcon(iid, null);
				}
				
				Object objectId = printers.getValue();
				printers.setItemIcon(objectId, new ThemeResource("pics/rowheader7.png"));
				
				//set data for fields
	            if (objectId != null){
	            	Item item = printers.getItem(objectId);	            	
				    editorFields.setItemDataSource(item);		
				    Object itemPodrId = item.getItemProperty("podrId").getValue();		    
				    if(itemPodrId != null /*&& itemPodrTitle != null*/){ //for add printer item
						Podrs podr = Factory.getInstance().getPodrsDAO().getPodrById(Integer.parseInt(itemPodrId.toString()));
					    cmbPodrs.select(podr);
					    fieldMatOtv.setValue(podr.getMatOtv());
					    fieldNumber.setValue(podr.getTelefon());					    
				    }else{
				    	cmbPodrs.setValue(null);
			    	
				    	//clear textfields
				    	fieldMatOtv.setValue("");
					    fieldNumber.setValue("");
				    }
				    
					editorLayout.setVisible(objectId != null);		
					printers.refreshRowCache();
						
					//hide hello and visible tabsheet
					helloLayout.setVisible(false);
					tabSheet.setVisible(true);
					 
					//set events table data source
			        Object itemPrinterId = item.getItemProperty("printerId").getValue();
			        if(itemPrinterId != null){
			           Printers printer = Factory.getInstance().getPrintersDAO().getPrinterById(Integer.parseInt(itemPrinterId.toString()));
			            	
			           //get all events for printer
			           List<Events> listEvetns = Factory.getInstance().getEventsDAO().getAllEventsByPrinter(printer);
			           eventsContainer.removeAllItems();
			            	
			           Iterator<Events> ite = listEvetns.iterator(); 
			           while(ite.hasNext()){
			        	   Events ev = ite.next();
			        	   Item newItem = eventsContainer.getItem(eventsContainer.addItem());
			        	   newItem.getItemProperty("id").setValue(ev.getId());
			        	   newItem.getItemProperty("description").setValue(ev.getDescription());
			        	   newItem.getItemProperty("date").setValue(ev.getDate());		        				    				  
			           }
			       	   events.setContainerDataSource(eventsContainer);
			    	   events.setVisibleColumns(new Object[]{ "description" ,"date" });
			       }
	           }else{
	        	   events.removeAllItems();
	           }
	        }
		 });				
	}
	
	private void initEventsTable(){
		events.setContainerDataSource(eventsContainer);
		events.setColumnHeader("description", "Событие");
		events.setColumnHeader("date", "Дата");
		events.setColumnExpandRatio("description", 5.0f);
		events.setColumnExpandRatio("date", 1.0f);		
		
		events.setColumnIcon("description", new ThemeResource("pics/event.png"));
		events.setColumnIcon("date", new ThemeResource("pics/date.png"));
		
		events.setRowHeaderMode(RowHeaderMode.INDEX);
		events.setColumnWidth(null, 30);	
		
		events.setImmediate(true);
		events.setSelectable(true);	
		
		events.addValueChangeListener(new ValueChangeListener() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void valueChange(ValueChangeEvent event) {
				for (Iterator i = events.getItemIds().iterator(); i.hasNext();) {
					int iid = (Integer) i.next();
					events.setItemIcon(iid, null);
				}
				
				events.refreshRowCache();
				
				Object objectId = event.getProperty().getValue();
				events.setItemIcon(objectId, new ThemeResource("pics/rowheader7.png"));
			}
		});
	}
	
	private void initPodrsTable(){

		podrsTable.setContainerDataSource(podrsContainer);
		podrsTable.setColumnHeader("title", "Наименование");
		podrsTable.setColumnHeader("matotv", "Мат. ответственный");
		podrsTable.setColumnHeader("telefon", "Телефон");
		podrsTable.setVisibleColumns(new Object[] {"title", "matotv", "telefon"});
		podrsTable.setColumnIcon("title", new ThemeResource("pics/organisation.png"));
		podrsTable.setColumnIcon("matotv", new ThemeResource("pics/person.png"));
		podrsTable.setColumnIcon("telefon", new ThemeResource("pics/telefon.png"));
		
		podrsTable.setRowHeaderMode(RowHeaderMode.INDEX);
		podrsTable.setColumnWidth(null, 30);	
		
		podrsTable.setImmediate(true);
		podrsTable.setSelectable(true);	
		podrsTable.setSizeFull();
		
		podrsTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				for (Iterator i = podrsTable.getItemIds().iterator(); i.hasNext();) {
					int iid = (Integer) i.next();
					podrsTable.setItemIcon(iid, null);
				}
				
				podrsTable.refreshRowCache();
				
				Object objectId = event.getProperty().getValue();
				podrsTable.setItemIcon(objectId, new ThemeResource("pics/rowheader7.png"));
			}
		});
	}

	private void initSearch() {
		searchField.setInputPrompt("Введите строку поиска...");
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		searchField.addTextChangeListener(new TextChangeListener() {
			  
		       public void textChange(final TextChangeEvent event) {
		          container.removeAllContainerFilters();
		          container.addContainerFilter(new PrinterFilter(event.getText()));
	           }
		});
	}	
	
	private void initAddSaveRemoveButtons(){
		addNewPrinterButton.addClickListener(new ClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				container.removeAllContainerFilters();
				Object itemId = container.addItemAt(0);
				printers.getContainerProperty(itemId, "invent").setValue("");
				printers.getContainerProperty(itemId, "model").setValue("");
				printers.select(itemId);
				
				tabSheet.setSelectedTab(0);	
			}
		});
		
		savePrinterButton.addClickListener(new ClickListener() {
			
			@SuppressWarnings({ "static-access", "unchecked" })
			@Override
			public void buttonClick(ClickEvent event) {
				//validation
				if (!fieldModel.isValid() || !cmbPodrs.isValid()) {
					Notification.show("Пожалуйста, заполните все обязательные поля (*).", Type.WARNING_MESSAGE);
		            return;
		        }
				
				Object objectId = printers.getValue();				
	            if (objectId != null){
	            	Item item = printers.getItem(objectId);
	            	Object itemPrinterId = item.getItemProperty("printerId").getValue();
	            	Podrs cmbValue = (Podrs)cmbPodrs.getValue();
	            	if(cmbValue != null){
		            	Integer podrId = cmbValue.getId();
	    		        String podrTitle = cmbValue.getTitle();
					    Podrs newPodr = Factory.getInstance().getPodrsDAO().getPodrById(podrId);
					    
					    //save podr matotv and telefon
					    newPodr.setMatOtv(fieldMatOtv.getValue());
					    newPodr.setTelefon(fieldNumber.getValue());
					    Factory.getInstance().getPodrsDAO().updatePodr(newPodr);
					    //refillPodrCmb();
					    
					    //refresh podr table
					    podrsContainer.removeAllItems();
					    List<Podrs> podrs = null;//dublicate!--------------------------------
						try{		
							podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
							Iterator<Podrs> itp = podrs.iterator();
							while(itp.hasNext()){
								Podrs podr = itp.next();
								Item newItem = podrsContainer.getItem(podrsContainer.addItem());
							    newItem.getItemProperty("id").setValue(podr.getId());
							    newItem.getItemProperty("title").setValue(podr.getTitle());
							    newItem.getItemProperty("matotv").setValue(podr.getMatOtv());
							    newItem.getItemProperty("telefon").setValue(podr.getTelefon());
							}
							podrsTable.setContainerDataSource(podrsContainer);
					    	podrsTable.setVisibleColumns(new Object[]{ "title", "matotv", "telefon" });
						}catch(Exception ex){
							log.info("Error when getting data from db(Podrs): " + ex.getMessage());
						}
					    
		            	if(itemPrinterId != null){	
		            		//update record
                            String itemInvent = item.getItemProperty("invent").getValue().toString();
                            String itemModel = item.getItemProperty("model").getValue().toString();
			            	Integer printerId = Integer.parseInt(itemPrinterId.toString());			            	            	
			            	
						    Printers printer = Factory.getInstance().getPrintersDAO().getPrinterById(printerId);
                            printer.setInvent(itemInvent);
                            printer.setModel(itemModel);
                            printer.setPodrs(newPodr);
						    Factory.getInstance().getPrintersDAO().updatePrinter(printer);
		            	}else{
		            		//add new record
		            		String itemInvent = item.getItemProperty("invent").getValue().toString();
		            		String itemModel = item.getItemProperty("model").getValue().toString();
		            		
		            		Printers printer = new Printers();
		            		printer.setInvent(itemInvent);
		            		printer.setModel(itemModel);
		            		printer.setPodrs(newPodr);
		            		Integer newPrinterId = Factory.getInstance().getPrintersDAO().addPrinter(printer);
		            		item.getItemProperty("printerId").setValue(newPrinterId);
		            		
		            	}
	            		//update data in table	      					    				
	    		        item.getItemProperty("podrId").setValue(podrId);		
	    		        item.getItemProperty("podrTitle").setValue(podrTitle);
	            	}
	            }
				
			}
		});
		
		removePrinterButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = printers.getValue();
				
				//confirm dialog
				final Window window = new Window(" Подтверждение удаления");
					
				// Center it in the browser window
			    window.center();
				window.setResizable(false);
				window.setIcon(new ThemeResource("pics/info.ico"));
					
			    // Open it in the UI
			    addWindow(window);
					
			    VerticalLayout confirmLayout = new VerticalLayout();
			    confirmLayout.setSizeUndefined();
			    confirmLayout.setMargin(true);
			        
			    Label label = new Label("Вы действительно хотите удалить принтер?");
					
				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);
				buttonLayout.setMargin(new MarginInfo(true, true, false, true));
				buttonLayout.setWidth("220px");
				Button okButton = new Button("Да");
				okButton.setWidth("100%");
				okButton.setIcon(new ThemeResource("pics/add.png"));
				Button cancelButton = new Button("Нет");
				cancelButton.setWidth("100%");
				cancelButton.setIcon(new ThemeResource("pics/cancel.png"));
				buttonLayout.addComponent(okButton);
				buttonLayout.addComponent(cancelButton);	
				buttonLayout.setExpandRatio(okButton, 1.0f);
				buttonLayout.setExpandRatio(cancelButton, 1.0f);
					
				confirmLayout.addComponent(label);
				confirmLayout.addComponent(buttonLayout);
				confirmLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
					
				window.setContent(confirmLayout);
					
				cancelButton.addClickListener(new ClickListener() {
						
					@Override
					public void buttonClick(ClickEvent event) {
						window.close();
						removeWindow(window);
					}
				});
					
				okButton.addClickListener(new ClickListener() {
						
					@Override
					public void buttonClick(ClickEvent event) {
						if(objectId != null){					
							Item item = printers.getItem(objectId);
			            	Object itemPrinterId = item.getItemProperty("printerId").getValue();
			            	printers.removeItem(objectId);
			                if(itemPrinterId != null){ 
			                	Printers printer = Factory.getInstance().getPrintersDAO()
			                			.getPrinterById(Integer.parseInt(itemPrinterId.toString()));
			                	Factory.getInstance().getPrintersDAO().deletePrinter(printer);
			                }
			            	printers.select(printers.firstItemId());
						}
						window.close();
						removeWindow(window);
					}
				});																						
			}
		});
	}
	
	private void initAddEditRemoveEventsButtons(){
		addNewEventButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				FormLayout editEventLayout = new FormLayout();
				editEventLayout.setSizeUndefined();
				editEventLayout.setMargin(true);
				
				final Window window = new Window("Добавить событие");
				window.setResizable(false);
				window.setClosable(false);
				
				// Center it in the browser window
		        window.center();
		        
		        // Open it in the UI
		        addWindow(window);
				window.setContent(editEventLayout);
				
				final TextField eventDiscription = new TextField("Описание:");
				eventDiscription.setInputPrompt("Введите описание события...");
				eventDiscription.setWidth("300px");
				final PopupDateField eventDate = new PopupDateField("Дата:");
				eventDate.setInputPrompt("Выберите дату...");
				eventDate.setWidth("300px");
				
				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);
				buttonLayout.setWidth("250px");
				Button saveEventButton = new Button("Сохранить");
				saveEventButton.setWidth("100%");
				saveEventButton.setIcon(new ThemeResource("pics/save.png"));
				Button cancelEventButton = new Button("Отмена");
				cancelEventButton.setWidth("100%");
				cancelEventButton.setIcon(new ThemeResource("pics/cancel.png"));
				buttonLayout.addComponent(saveEventButton);
				buttonLayout.addComponent(cancelEventButton);
				
				buttonLayout.setExpandRatio(saveEventButton, 1.0f);
				buttonLayout.setExpandRatio(cancelEventButton, 1.0f);
				
				saveEventButton.addClickListener(new ClickListener() {
					
					@Override
 					public void buttonClick(ClickEvent event) {
						Object itemId = printers.getValue();

						if(itemId != null){			
							String description = eventDiscription.getValue();
							Date date = (Date) eventDate.getValue();
							if(description != "" && date != null){
								Events ev = new Events();
								ev.setDescription(description);
								
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					               
				                //to convert Date to String, use format method of SimpleDateFormat class.
				                String strDate = dateFormat.format(date);
								ev.setDate(strDate);														
								
								Item itemPrinterTable = printers.getItem(itemId);
								Object printerId = itemPrinterTable.getItemProperty("printerId").getValue(); 
								
								if(printerId != null){
									Printers printer = Factory.getInstance().getPrintersDAO().getPrinterById(
											Integer.parseInt(printerId.toString()));
									ev.setPrinters(printer);
									Integer newEventId = Factory.getInstance().getEventsDAO().addEvent(ev);
									
									Object objectId = eventsContainer.addItemAt(0);
									
									Item itemEventTable = events.getItem(objectId);
									itemEventTable.getItemProperty("id").setValue(newEventId);
									itemEventTable.getItemProperty("description").setValue(ev.getDescription());
									itemEventTable.getItemProperty("date").setValue(ev.getDate());
									events.select(objectId);
								}
							}else {
								Notification.show("Пожалуйста, заполните все поля.", Type.WARNING_MESSAGE);
								return;
							}
							events.refreshRowCache();
							window.close();
							removeWindow(window);
						}else{
							Notification.show("Пожалуйста, выберите принтер.", Type.WARNING_MESSAGE);
						}
					}
				});
				
				cancelEventButton.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {	
						events.refreshRowCache();
						window.close();
						removeWindow(window);
					}
				});
								
				editEventLayout.addComponent(eventDiscription);
				editEventLayout.addComponent(eventDate);
				editEventLayout.addComponent(buttonLayout);								
			}
		});
		
		editEventButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = events.getValue();
				if(objectId != null){					
					final Item item = events.getItem(objectId);
					Object itemDescription = item.getItemProperty("description").getValue();
					Object itemDate = item.getItemProperty("date").getValue();
					if(itemDescription !=null && itemDate != null){		
						FormLayout editEventLayout = new FormLayout();
						editEventLayout.setSizeUndefined();
						editEventLayout.setMargin(true);
						
						final Window window = new Window("Редактировать событие");
						window.setResizable(false);
						window.setClosable(false);
						
						// Center it in the browser window
				        window.center();
				        
				        // Open it in the UI
				        addWindow(window);
						
						final TextField eventDiscription = new TextField("Описание:");
						eventDiscription.setValue(itemDescription.toString());
						eventDiscription.setWidth("300px");
						final PopupDateField eventDate = new PopupDateField("Дата:");
                        eventDate.setDateFormat("dd.MM.yyyy");
                        SimpleDateFormat textFormat = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            eventDate.setValue(textFormat.parse(itemDate.toString()));
                        }catch(ParseException e){
                            eventDate.setValue(new Date());
                        }
						eventDate.setWidth("300px");
						
						HorizontalLayout buttonLayout = new HorizontalLayout();
						buttonLayout.setSpacing(true);
						buttonLayout.setWidth("250px");
						Button saveEventButton = new Button("Сохранить");
						saveEventButton.setWidth("100%");
						saveEventButton.setIcon(new ThemeResource("pics/save.png"));
						Button cancelEventButton = new Button("Отмена");
						cancelEventButton.setWidth("100%");
						cancelEventButton.setIcon(new ThemeResource("pics/cancel.png"));
						buttonLayout.addComponent(saveEventButton);
						buttonLayout.addComponent(cancelEventButton);	
						buttonLayout.setExpandRatio(saveEventButton, 1.0f);
						buttonLayout.setExpandRatio(cancelEventButton, 1.0f);
						
						saveEventButton.addClickListener(new ClickListener() {
							
							@Override
		 					public void buttonClick(ClickEvent event) {
								String description = eventDiscription.getValue();
								Date date = (Date) eventDate.getValue();
								if(description != "" && date != null){
									Object eventId = item.getItemProperty("id").getValue();
									Events ev = Factory.getInstance().getEventsDAO().getEventById(
											Integer.parseInt(eventId.toString()));
									ev.setDescription(description);
									
									DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
					                //to convert Date to String, use format method of SimpleDateFormat class.
					                String strDate = dateFormat.format(date);
									ev.setDate(strDate);
									
									Factory.getInstance().getEventsDAO().updateEvent(ev);
									Item itemEventTable = events.getItem(objectId);
									itemEventTable.getItemProperty("description").setValue(ev.getDescription());
									itemEventTable.getItemProperty("date").setValue(ev.getDate());
								}else {
									Notification.show("Пожалуйста, заполните все поля.", Type.WARNING_MESSAGE);
									return;
								}
								events.refreshRowCache();
								window.close();
								removeWindow(window);
							}
						});
						
						cancelEventButton.addClickListener(new ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								window.close();
								removeWindow(window);
							}
						});
						
						editEventLayout.addComponent(eventDiscription);
						editEventLayout.addComponent(eventDate);
						editEventLayout.addComponent(buttonLayout);
						
						window.setContent(editEventLayout);
					}
				}
			}
		});
		
		removeEventButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = events.getValue();
				if(objectId != null){ //dont create window if do not select item
					//confirm dialog
					final Window window = new Window(" Подтверждение удаления");
					
					// Center it in the browser window
			        window.center();
					window.setResizable(false);
					window.setClosable(false);
					window.setIcon(new ThemeResource("pics/info.ico"));
					
			        // Open it in the UI
			        addWindow(window);
					
			        VerticalLayout confirmLayout = new VerticalLayout();
			        confirmLayout.setSizeUndefined();
			        confirmLayout.setMargin(true);
			        
					Label label = new Label("Вы действительно хотите удалить событие?");
					
					HorizontalLayout buttonLayout = new HorizontalLayout();
					buttonLayout.setSpacing(true);
					buttonLayout.setMargin(new MarginInfo(true, true, false, true));
					buttonLayout.setWidth("220px");
					Button okEventButton = new Button("Да");
					okEventButton.setWidth("100%");
					okEventButton.setIcon(new ThemeResource("pics/add.png"));
					Button cancelEventButton = new Button("Нет");
					cancelEventButton.setWidth("100%");
					cancelEventButton.setIcon(new ThemeResource("pics/cancel.png"));
					buttonLayout.addComponent(okEventButton);
					buttonLayout.addComponent(cancelEventButton);	
					buttonLayout.setExpandRatio(okEventButton, 1.0f);
					buttonLayout.setExpandRatio(cancelEventButton, 1.0f);
					
					confirmLayout.addComponent(label);
					confirmLayout.addComponent(buttonLayout);
					confirmLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
					
					window.setContent(confirmLayout);
					
					cancelEventButton.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							window.close();
							removeWindow(window);
						}
					});
					
					okEventButton.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							if(objectId != null){					
								Item item = events.getItem(objectId);
								Object eventId = item.getItemProperty("id").getValue();
								Events ev = Factory.getInstance().getEventsDAO().getEventById(
											Integer.parseInt(eventId.toString()));
								Factory.getInstance().getEventsDAO().deleteEvent(ev);
								events.removeItem(objectId);
							}
							window.close();
							removeWindow(window);
						}
					});				
				}
			}
		});
	}

	private void initAddEditRemovePodrsButons(){

		addNewPodrButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = podrsContainer.addItemAt(0);
				podrsTable.select(objectId);
				
				FormLayout editPodrLayout = new FormLayout();
				editPodrLayout.setSizeUndefined();
				editPodrLayout.setMargin(true);
				
				final Window window = new Window("Добавить подразделение");
				window.setResizable(false);
				window.setClosable(false);
				
				// Center it in the browser window
		        window.center();
		        
		        // Open it in the UI
		        addWindow(window);
				window.setContent(editPodrLayout);
				
				final TextField title = new TextField("Наименование:");
				title.setInputPrompt("Введите наименование...");
				title.setWidth("300px");
				final TextField matOtv = new TextField("Mat. ответственный:");
				matOtv.setInputPrompt("Введите ФИО...");
				matOtv.setWidth("300px");
				final TextField telefone = new TextField("Телефон:");
				telefone.setInputPrompt("Введите номер телефона...");
				telefone.setWidth("300px");
				
				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);
				buttonLayout.setWidth("250px");
				Button savePodrButton = new Button("Сохранить");
				savePodrButton.setWidth("100%");
				savePodrButton.setIcon(new ThemeResource("pics/save.png"));
				Button cancelPodrButton = new Button("Отмена");
				cancelPodrButton.setWidth("100%");
				cancelPodrButton.setIcon(new ThemeResource("pics/cancel.png"));
				buttonLayout.addComponent(savePodrButton);
				buttonLayout.addComponent(cancelPodrButton);
				
				buttonLayout.setExpandRatio(savePodrButton, 1.0f);
				buttonLayout.setExpandRatio(cancelPodrButton, 1.0f);
				
				savePodrButton.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						String titleVal = title.getValue();
						String matString = matOtv.getValue();
						String telefonString = telefone.getValue();
						if(titleVal != ""){
							Podrs podr = new Podrs();
							podr.setTitle(titleVal);
							podr.setMatOtv(matString);
							podr.setTelefon(telefonString);
																				
							Integer newPodrId = Factory.getInstance().getPodrsDAO().addPodr(podr);
		            		
							Item itemPodrTable = podrsTable.getItem(objectId);
							itemPodrTable.getItemProperty("id").setValue(newPodrId);
							itemPodrTable.getItemProperty("title").setValue(podr.getTitle());
							itemPodrTable.getItemProperty("matotv").setValue(podr.getMatOtv());
							itemPodrTable.getItemProperty("telefon").setValue(podr.getTelefon());
							
							//refill cmb and select old value
							refillPodrCmb();
						}else {
							Notification.show("Пожалуйста, укажите наименование подразделения.", Type.WARNING_MESSAGE);
							return;
						}
						podrsTable.refreshRowCache();
																	
						window.close();
						removeWindow(window);			
					}
				});
			
				cancelPodrButton.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						podrsContainer.removeItem(objectId);	
						podrsTable.refreshRowCache();
						window.close();
						removeWindow(window);
					}
				});
				
				editPodrLayout.addComponent(title);
				editPodrLayout.addComponent(matOtv);
				editPodrLayout.addComponent(telefone);
				editPodrLayout.addComponent(buttonLayout);							
			}
		});
		
		editPodrButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = podrsTable.getValue();
				if(objectId != null){					
					final Item item = podrsTable.getItem(objectId);
					Object itemTitle = item.getItemProperty("title").getValue();
					Object itemMatOtv = item.getItemProperty("matotv").getValue();
					Object itemTelefone = item.getItemProperty("telefon").getValue();
					if(itemTitle !=null){//-----						
						FormLayout editPodrLayout = new FormLayout();
						editPodrLayout.setSizeUndefined();
						editPodrLayout.setMargin(true);
						
						final Window window = new Window("Редактировать подразделение");
						window.setResizable(false);
						window.setClosable(false);
						
						// Center it in the browser window
				        window.center();
				        
				        // Open it in the UI
				        addWindow(window);
						
						final TextField podrTitle = new TextField("Наименование:");
						podrTitle.setValue(itemTitle.toString());
						podrTitle.setWidth("300px");
						
						final TextField podrMatOtv = new TextField("Мат. ответственный:");
						podrMatOtv.setInputPrompt("Введите ФИО...");
						podrMatOtv.setNullSettingAllowed(true);
						podrMatOtv.setValue(itemMatOtv.toString());
						podrMatOtv.setWidth("300px");
						
						final TextField podrTelefone = new TextField("Телефон:");
						podrTelefone.setInputPrompt("Введите номер телефона...");
						podrTelefone.setNullSettingAllowed(true);
						podrTelefone.setValue(itemTelefone.toString());
						podrTelefone.setWidth("300px");
						
						HorizontalLayout buttonLayout = new HorizontalLayout();
						buttonLayout.setSpacing(true);
						buttonLayout.setWidth("250px");
						Button savePodrButton = new Button("Сохранить");
						savePodrButton.setWidth("100%");
						savePodrButton.setIcon(new ThemeResource("pics/save.png"));
						Button cancelPodrButton = new Button("Отмена");
						cancelPodrButton.setWidth("100%");
						cancelPodrButton.setIcon(new ThemeResource("pics/cancel.png"));
						buttonLayout.addComponent(savePodrButton);
						buttonLayout.addComponent(cancelPodrButton);	
						buttonLayout.setExpandRatio(savePodrButton, 1.0f);
						buttonLayout.setExpandRatio(cancelPodrButton, 1.0f);
						
						savePodrButton.addClickListener(new ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								String titleString = podrTitle.getValue();
								String matOtvString = podrMatOtv.getValue();
								String telefonString = podrTelefone.getValue();
								if(titleString != ""){
									Object podrId = item.getItemProperty("id").getValue();
									Podrs podr = Factory.getInstance().getPodrsDAO().getPodrById(
											Integer.parseInt(podrId.toString()));
									podr.setTitle(titleString);
									podr.setMatOtv(matOtvString);
									podr.setTelefon(telefonString);
									
									Factory.getInstance().getPodrsDAO().updatePodr(podr);
									Item itemPodrTable = podrsTable.getItem(objectId);
									itemPodrTable.getItemProperty("title").setValue(podr.getTitle());
									itemPodrTable.getItemProperty("matotv").setValue(podr.getMatOtv());
									itemPodrTable.getItemProperty("telefon").setValue(podr.getTelefon());
									
									//refill cmb and select old value
									refillPodrCmb();
								}else {
									Notification.show("Пожалуйста, укажите наименование подразделения.", Type.WARNING_MESSAGE);
									return;
								}
								podrsTable.refreshRowCache();
								window.close();
								removeWindow(window);								
							}
						});
						
						cancelPodrButton.addClickListener(new ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								window.close();
								removeWindow(window);
							}
						});
						
						editPodrLayout.addComponent(podrTitle);
						editPodrLayout.addComponent(podrMatOtv);
						editPodrLayout.addComponent(podrTelefone);
						editPodrLayout.addComponent(buttonLayout);
						
						window.setContent(editPodrLayout);
					}
				}
				
			}
		});
		
		removePodrButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Object objectId = podrsTable.getValue();
				if(objectId != null){ //dont create window if do not select item
					//confirm dialog
					final Window window = new Window(" Подтверждение удаления");
					
					// Center it in the browser window
			        window.center();
					window.setResizable(false);
					window.setClosable(false);
					window.setIcon(new ThemeResource("pics/info.ico"));
					
			        // Open it in the UI
			        addWindow(window);
					
			        VerticalLayout confirmLayout = new VerticalLayout();
			        confirmLayout.setSizeUndefined();
			        confirmLayout.setMargin(true);
			        
					Label label = new Label("Вы действительно хотите удалить подразделение?");
					
					HorizontalLayout buttonLayout = new HorizontalLayout();
					buttonLayout.setSpacing(true);
					buttonLayout.setMargin(new MarginInfo(true, true, false, true));
					buttonLayout.setWidth("220px");
					Button okButton = new Button("Да");
					okButton.setWidth("100%");
					okButton.setIcon(new ThemeResource("pics/add.png"));
					Button cancelButton = new Button("Нет");
					cancelButton.setWidth("100%");
					cancelButton.setIcon(new ThemeResource("pics/cancel.png"));
					buttonLayout.addComponent(okButton);
					buttonLayout.addComponent(cancelButton);	
					buttonLayout.setExpandRatio(okButton, 1.0f);
					buttonLayout.setExpandRatio(cancelButton, 1.0f);
					
					confirmLayout.addComponent(label);
					confirmLayout.addComponent(buttonLayout);
					confirmLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
					
					window.setContent(confirmLayout);
				
					cancelButton.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							window.close();
							removeWindow(window);
						}
					});
					
					okButton.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							if(objectId != null){					
								Item item = podrsTable.getItem(objectId);
								Object podrId = item.getItemProperty("id").getValue();
								Podrs podr = Factory.getInstance().getPodrsDAO().getPodrById(
											Integer.parseInt(podrId.toString()));
								Factory.getInstance().getPodrsDAO().deletePodr(podr);
								podrsTable.removeItem(objectId);
								
								//refill cmb and select old value
								refillPodrCmb();
								loadDataInPrintersContainer(container);
							}
							window.close();
							removeWindow(window);
						}
					});
				}
			}
		});
	}
	
	private IndexedContainer createPrintersDataSource(){
		IndexedContainer container = new IndexedContainer();		
		
		container.addContainerProperty("podrId", Integer.class, null);
		container.addContainerProperty("invent", String.class, null);
		container.addContainerProperty("podrTitle", String.class, null);
		container.addContainerProperty("printerId", Integer.class, null);
		container.addContainerProperty("model", String.class, null);		
		
		loadDataInPrintersContainer(container);
		
		return container;
	}
	
	private IndexedContainer loadDataInPrintersContainer(IndexedContainer container){
		container.removeAllItems();
		List<Podrs> podrs = null;
		try{
			podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
			Iterator<Podrs> itp = podrs.iterator(); 
			while(itp.hasNext()){
				Podrs podr = itp.next();
				Set<Printers> printers = podr.getPrinters();
				for(Printers printer : printers){					
					Item newItem = container.getItem(container.addItem());
				    newItem.getItemProperty("podrId").setValue(podr.getId());
				    newItem.getItemProperty("podrTitle").setValue(podr.getTitle());
				    newItem.getItemProperty("printerId").setValue(printer.getId());
				    newItem.getItemProperty("invent").setValue(printer.getInvent());
				    newItem.getItemProperty("model").setValue(printer.getModel());			    				  
				}
			}
		}catch(Exception ex){
			log.info("Error when getting data from db(Podrs): " + ex.getMessage());
		}		
		return container;
	}
	
	private IndexedContainer createEventsDataSource(){
		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("id", Integer.class, null);
		container.addContainerProperty("description", String.class, null);
		container.addContainerProperty("date", String.class, null);
		return container;
	}
	
	private IndexedContainer createPodrsDataSource(){
		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("id", Integer.class, null);
		container.addContainerProperty("title", String.class, null);
		container.addContainerProperty("matotv", String.class, null);
		container.addContainerProperty("telefon", String.class, null);
		List<Podrs> podrs = null;//dublicate!
		try{		
			podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
			Iterator<Podrs> itp = podrs.iterator();
			while(itp.hasNext()){
				Podrs podr = itp.next();
				Item newItem = container.getItem(container.addItem());
			    newItem.getItemProperty("id").setValue(podr.getId());
			    newItem.getItemProperty("title").setValue(podr.getTitle());
			    newItem.getItemProperty("matotv").setValue(podr.getMatOtv());
			    newItem.getItemProperty("telefon").setValue(podr.getTelefon());
			}
		}catch(Exception ex){
			log.info("Error when getting data from db(Podrs): " + ex.getMessage());
		}
		return container;
	}

    private void initReportLayout(){
        Label lb1 = new Label("Отчет №1: по подразделению");
        List<Podrs> podrs = Factory.getInstance().getPodrsDAO().getAllPodrs();
        podrsContainerCmb = new BeanItemContainer<Podrs>(Podrs.class ,podrs);
        reportPodrs = new ComboBox("Подразделение:", podrsContainerCmb);
        reportPodrs.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        reportPodrs.setItemCaptionPropertyId("title");
        reportPodrs.setNullSelectionAllowed(false);
        reportPodrs.setInputPrompt("Выберите подразделение...");
        reportPodrs.setRequiredError("Необходимо указать подразделение.");
        reportPodrs.setImmediate(true);
        reportPodrs.setWidth("100%");

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.addComponent(reportButton);
        hLayout.setWidth("100%");
        hLayout.setComponentAlignment(reportButton, Alignment.MIDDLE_CENTER);

        reportLayout.addComponent(lb1);
        reportLayout.addComponent(reportPodrs);
        reportLayout.addComponent(hLayout);
        reportLayout.setComponentAlignment(reportPodrs, Alignment.TOP_CENTER);
        //reportLayout.setVisible(false);
        reportLayout.setMargin(true);

        reportButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if(reportPodrs.getValue() != null) {
                    showReport1(((Podrs) reportPodrs.getValue()).getId());
                }
            }
        });

        final PopupDateField startDate = new PopupDateField("C:");
        startDate.setInputPrompt("Выберите дату...");
        startDate.setWidth("300px");

        final PopupDateField endDate = new PopupDateField("По:");
        endDate.setInputPrompt("Выберите дату...");
        endDate.setWidth("300px");

        HorizontalLayout hLayout2 = new HorizontalLayout();
        hLayout2.addComponent(report2Button);
        HorizontalLayout hLayout3 = new HorizontalLayout();
        hLayout3.addComponent(report2ButtonXLS);
        hLayout2.setWidth("100%");
        hLayout2.setComponentAlignment(report2Button, Alignment.MIDDLE_CENTER);
        hLayout3.setWidth("100%");
        hLayout3.setComponentAlignment(report2ButtonXLS, Alignment.MIDDLE_CENTER);

        Label lb2 = new Label("Отчет №2: по всем подразделениям");
        reportLayout.addComponent(lb2);
        reportLayout.addComponent(startDate);
        reportLayout.addComponent(endDate);
        reportLayout.addComponent(hLayout2);
        reportLayout.addComponent(hLayout3);

        report2Button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if(startDate.getValue() != null && endDate.getValue() != null) {
                    showReport2PDF(startDate.getValue(), endDate.getValue());
                }
            }
        });


        FileDownloader downloader = new FileDownloader(new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                File file = new File("/home/jboss/report2.csv");
                try {
                    //file.mkdirs();
                    log.info("File name: " + file.getName());
                    log.severe("File name: " + file.getName());
                    log.severe("Creating file...");
                    if(file.createNewFile()){
                        log.severe("File created -----------------------");
                    }
                    //clear file
                    PrintWriter writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();

                    if(startDate.getValue() != null && endDate.getValue() != null) {
                        return generateReport2XLS(startDate.getValue(), endDate.getValue(), file);
                    }
                    return null;
                } catch(FileNotFoundException nfd){
                    nfd.printStackTrace();
                } catch(IOException io){
                    io.printStackTrace();
                }
                return null;
            }
        }, "report2.csv"));
        downloader.extend(report2ButtonXLS);

        //StreamResource sr = getFileStream("/tmp/report2.csv");
        //FileDownloader fileDownloader = new FileDownloader(sr);
        //fileDownloader.extend(report2ButtonXLS);
    }

//    private StreamResource getFileStream(final String fileName) {
//        StreamResource.StreamSource source = new StreamResource.StreamSource() {
//
//            public InputStream getStream() {
//                File file = new File(fileName);
//                try {
//                    if(startDate.getValue() != null && endDate.getValue() != null) {
//                        showReport2XLS(startDate.getValue(), endDate.getValue());
//                    }
//                    return new FileInputStream(file);
//                } catch(FileNotFoundException nfd){
//                    nfd.printStackTrace();
//                }
//                return null;
//
//            }
//        };
//
//        StreamResource resource = new StreamResource(source, "report2.csv");
//        resource.setMIMEType("text/csv");
//
//        return resource;
//    }

    private void showReport1(final int podr_id){
        try {
            StreamResource.StreamSource source = new StreamResource.StreamSource() {

                public InputStream getStream() {
                    byte[] b = null;
                    try {
                        HashMap map = new HashMap();
                        map.put("dt", new Date());
                        map.put("podr_id", podr_id);

                        Session session = HibernateUtil.getSessionFactory().openSession();
                        SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
                        ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();
                        Connection connection = null;
                        try {
                            connection = connectionProvider.getConnection();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        b = JasperRunManager.runReportToPdf(getClass().getClassLoader().getResourceAsStream("reports/report1.jasper"), map, connection);
                    } catch (JRException ex) {
                        //Logger.getLogger(TokenForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return new ByteArrayInputStream(b);
                }
            };

            StreamResource resource = new StreamResource(source, "report1.pdf");
            resource.setMIMEType("application/pdf");

            viewDocument(resource);
        } catch (Exception ex) {
            //Logger.getLogger(TokenForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showReport2PDF(final Date dt1, final Date dt2){
        try {
            StreamResource.StreamSource source = new StreamResource.StreamSource() {

                public InputStream getStream() {
                    byte[] b = null;
                    try {
                        HashMap map = new HashMap();
                        map.put("dt1", dt1);
                        map.put("dt2", dt2);

                        Session session = HibernateUtil.getSessionFactory().openSession();
                        SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
                        ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();
                        Connection connection = null;
                        try {
                            connection = connectionProvider.getConnection();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        b = JasperRunManager.runReportToPdf(getClass().getClassLoader().getResourceAsStream("reports/report2.jasper"), map, connection);
                    } catch (JRException ex) {
                        //Logger.getLogger(TokenForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return new ByteArrayInputStream(b);
                }
            };

            StreamResource resource = new StreamResource(source, "report2.pdf");
            resource.setMIMEType("application/pdf");

            viewDocument(resource);
        } catch (Exception ex) {
            //Logger.getLogger(TokenForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private InputStream generateReport2XLS(final Date dt1, final Date dt2, final File file){
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
            ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();
            Connection connection = null;
            try {
                connection = connectionProvider.getConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            Statement stmt = null;
            SimpleDateFormat df = new SimpleDateFormat("MM.dd.yyyy");
            String query = "SELECT p.title as podr, COUNT(ev.description) as cnt from podrs p " +
                           "LEFT JOIN printers pr on p.id=pr.podrid " +
                           "LEFT JOIN events ev on pr.id=ev.printerid " +
                           "WHERE to_date(ev.date, 'DD.MM.YYYY') >= '" + df.format(dt1) + "' and to_date(ev.date, 'DD.MM.YYYY') <= '" + df.format(dt2) + "' and ev.description NOT LIKE '%емонт%' " +
                           "GROUP BY p.title " +
                           "ORDER BY p.title";
            log.severe(query);
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            try{
                PrintWriter writer = new PrintWriter(file, "Cp1252");
                while (rs.next()) {
                    String podr = rs.getString("podr");
                    int cnt = rs.getInt("cnt");
                    //log.severe(podr + ";" + cnt);
                    writer.println(podr + ";" + cnt);
                }
                writer.close();
                return new FileInputStream(file);
            } catch (IOException io){
                io.printStackTrace();
            }

//            StreamResource.StreamSource source = new StreamResource.StreamSource() {
//                @Override
//                public InputStream getStream() {
//                    try {
//                        return new FileInputStream(file);
//                    } catch(FileNotFoundException nfd){
//                        nfd.printStackTrace();
//                    }
//                    return null;
//                }
//            };
//
//            StreamResource resource = new StreamResource(source, "report2.csv");
//            resource.setMIMEType("text/csv");
//
//            return resource;
        }catch (SQLException ex){
           ex.printStackTrace();
        }
        return null;
    }

    private void viewDocument(StreamResource resource)
    {
        Embedded c = new Embedded("", resource);
        c.setSizeFull();
        c.setType(Embedded.TYPE_BROWSER);

        Window window = new Window("Отчет", c);
        window.setCaption("Отчет");
        window.getContent().setSizeFull();
        window.setModal(true);
        window.setWidth("90%");
        window.setHeight("90%");

        this.addWindow(window);
    }
			
	private void initLogger(){
		try {
			FileHandler fileHandler = new FileHandler("app.log", true);
			fileHandler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + "\n";//------edit this!!!
				}
			});
			log.addHandler(fileHandler);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}