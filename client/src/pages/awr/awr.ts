import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

import { AwrSinglePage } from './single/single'

@Component({
  selector: 'page-awr',
  templateUrl: 'awr.html'
})
export class AwrPage {

  constructor(public navCtrl: NavController, public navParams: NavParams) { }

  toPage() {
    this.navCtrl.push(AwrSinglePage);
  }
}
