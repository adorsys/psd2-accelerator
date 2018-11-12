import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo() {
    return browser.get('/');
  }

  getDescriptionTitle() {
    return element(by.css('.description__title')).getText();
  }

  clickDownloadButton() {
    element(by.css('.btn-primary')).click();
  }
}
