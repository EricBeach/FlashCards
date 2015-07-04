var Navigation = {};

Navigation.showInitialViewOnPage = function() {
  if (window.location.hash) {
    switch (window.location.hash) {
      case '#history':
        Navigation.showHistoryView();
        document.getElementById('history-menu-btn').classList.add('core-selected');
        break;
      case '#about':
        Navigation.showAboutView();
        document.getElementById('about-menu-btn').classList.add('core-selected');
        break;
      case '#settings':
        Navigation.showSettingsView();
        document.getElementById('settings-menu-btn').classList.add('core-selected');
        break;
      case '#card-series-generator':
        Navigation.showFlashCardsSeriesView();
        document.getElementById('flash-cards-series-menu-btn').classList.add('core-selected');
        break;
      default:
        Navigation.showFlashCardsHomeView();
        document.getElementById('home-menu-btn').classList.add('core-selected');
    }
  } else {
    Navigation.showFlashCardsHomeView();
    document.getElementById('home-menu-btn').classList.add('core-selected');
  }
};


Navigation.removeFoucProtection = function() {
  var elements = document.querySelectorAll('.fouc-prevention');
  for (var i = 0; i < elements.length; i++) {
    elements[i].classList.remove('fouc-prevention');
  }
};


Navigation.hideAllVerticalContent = function() {
  var elements = document.querySelectorAll('vertical-content-body');
  for (var i = 0; i < elements.length; i++) {
    elements[i].classList.remove('show-element');
    elements[i].classList.add('hide-element');
  }
};


Navigation.hideAllCenteredContent = function() {
  var elements = document.querySelectorAll('.view--centered-content');
  for (var i = 0; i < elements.length; i++) {
    elements[i].classList.remove('show-element');
    elements[i].classList.add('hide-element');
  }
};


Navigation.removeAllMenuItemSelectedIndication = function() {
  var elements = document.querySelectorAll('.core-selected');
  for (var i = 0; i < elements.length; i++) {
    elements[i].classList.remove('core-selected');
  }
};


Navigation.hideAllContent = function() {
  Navigation.hideAllVerticalContent();
  Navigation.hideAllCenteredContent();
  Navigation.removeAllMenuItemSelectedIndication();
};


Navigation.showFlashCardsHomeView = function() {
  var element = document.getElementById('view--flash-cards-home');
  element.classList.remove('hide-element');
};


Navigation.showSettingsView = function() {
  var element = document.getElementById('view--settings');
  element.classList.remove('hide-element');
};


Navigation.showFlashCardsSeriesView = function() {
  var element = document.getElementById('view--series-generator');
  element.classList.remove('hide-element');
};


Navigation.showAboutView = function() {
  var element = document.getElementById('view--about');
  element.classList.remove('hide-element');
  element.classList.add('show-element');
};


Navigation.showHistoryView = function() {
  var element = document.getElementById('view--history');
  element.classList.remove('hide-element');
  element.classList.add('show-element');
};


Navigation.loadFlashCardSeriesWithIds = function(ids) {
  document.getElementById('view--flash-cards-home--contents').innerHTML =
    '<flash-card-series card-number-limit="4" style="width: 100%; height: 100%; display: block;"></flash-card-series>';
  Navigation.hideAllContent();
  Navigation.showFlashCardsHomeView();
};


document.addEventListener('polymer-ready', function() {
  var drawerPanel = document.getElementById('drawerPanel');
  var navicon = document.getElementById('navicon');  
  navicon.addEventListener('click', function() {
    drawerPanel.togglePanel();
  });

  var homeBtn = document.getElementById('home-menu-btn');
  homeBtn.addEventListener('click', function() {
    Navigation.hideAllContent();
    Navigation.showFlashCardsHomeView();
    drawerPanel.closeDrawer();
  });

  var aboutBtn = document.getElementById('about-menu-btn');
  aboutBtn.addEventListener('click', function() {
    Navigation.hideAllContent();
    Navigation.showAboutView();
    drawerPanel.closeDrawer();
  });

  var historyBtn = document.getElementById('history-menu-btn');
  historyBtn.addEventListener('click', function() {
    Navigation.hideAllContent();
    Navigation.showHistoryView();
    drawerPanel.closeDrawer();
  });

  var settingsBtn = document.getElementById('settings-menu-btn');
  settingsBtn.addEventListener('click', function() {
    Navigation.hideAllContent();
    Navigation.showSettingsView();
    drawerPanel.closeDrawer();
  });

  var flashCardsBtn = document.getElementById('flash-cards-series-menu-btn');
  flashCardsBtn.addEventListener('click', function() {
    Navigation.hideAllContent();
    Navigation.showFlashCardsSeriesView();
    drawerPanel.closeDrawer();
  });

  Navigation.removeFoucProtection();
  Navigation.showInitialViewOnPage();
});
