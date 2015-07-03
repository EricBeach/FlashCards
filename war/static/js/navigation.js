document.addEventListener('polymer-ready', function() {
  function showInitialViewOnPage() {
    if (window.location.hash) {
      switch (window.location.hash) {
        case '#history':
          showHistoryView();
          document.getElementById('history-menu-btn').classList.add('core-selected');
          break;
        case '#about':
          showAboutView();
          document.getElementById('about-menu-btn').classList.add('core-selected');
          break;
        case '#settings':
          showSettingsView();
          document.getElementById('settings-menu-btn').classList.add('core-selected');
          break;
        default:
          showFlashCardsView();
          document.getElementById('flash-cards-menu-btn').classList.add('core-selected');
      }
    } else {
      showFlashCardsView();
      document.getElementById('flash-cards-menu-btn').classList.add('core-selected');
    }
  }

  function removeFoucProtection() {
    var elements = document.querySelectorAll('.fouc-prevention');
    for (var i = 0; i < elements.length; i++) {
      elements[i].classList.remove('fouc-prevention');
    }
  }

  function hideAllVerticalContent() {
    var elements = document.querySelectorAll('vertical-content-body');
    for (var i = 0; i < elements.length; i++) {
      elements[i].classList.remove('show-element');
      elements[i].classList.add('hide-element');
    }
  }

  function hideAllCenteredContent() {
    var elements = document.querySelectorAll('.view--centered-content');
    for (var i = 0; i < elements.length; i++) {
      elements[i].classList.remove('show-element');
      elements[i].classList.add('hide-element');
    }
  }

  function removeAllMenuItemSelectedIndication() {
    var elements = document.querySelectorAll('.core-selected');
    for (var i = 0; i < elements.length; i++) {
      elements[i].classList.remove('core-selected');
    }
  }

  function hideAllContent() {
    hideAllVerticalContent();
    hideAllCenteredContent();
    removeAllMenuItemSelectedIndication();
  }

  function showSettingsView() {
    var element = document.getElementById('view--settings');
    element.classList.remove('hide-element');
  }

  function showFlashCardsView() {
    var element = document.getElementById('view--flash-cards');
    element.classList.remove('hide-element');
  }

  function showAboutView() {
    var element = document.getElementById('view--about');
    element.classList.remove('hide-element');
    element.classList.add('show-element');
  }

  function showHistoryView() {
    var element = document.getElementById('view--history');
    element.classList.remove('hide-element');
    element.classList.add('show-element');
  }

  var drawerPanel = document.getElementById('drawerPanel');
  var navicon = document.getElementById('navicon');  
  navicon.addEventListener('click', function() {
    drawerPanel.togglePanel();
  });

  var aboutBtn = document.getElementById('about-menu-btn');
  aboutBtn.addEventListener('click', function() {
    hideAllContent();
    showAboutView();
    drawerPanel.closeDrawer();
  });

  var historyBtn = document.getElementById('history-menu-btn');
  historyBtn.addEventListener('click', function() {
    hideAllContent();
    showHistoryView();
    drawerPanel.closeDrawer();
  });

  var settingsBtn = document.getElementById('settings-menu-btn');
  settingsBtn.addEventListener('click', function() {
    hideAllContent();
    showSettingsView();
    drawerPanel.closeDrawer();
  });

  var flashCardsBtn = document.getElementById('flash-cards-menu-btn');
  flashCardsBtn.addEventListener('click', function() {
    hideAllContent();
    showFlashCardsView();
    drawerPanel.closeDrawer();
  });

  removeFoucProtection();
  showInitialViewOnPage();
});