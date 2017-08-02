import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

@Component({
  selector: 'page-topsql',
  templateUrl: 'topsql.html'
})
export class TopSqlPage {

  constructor(public navCtrl: NavController, public navParams: NavParams) { }

  tops = ["IO WAIT", "EXECUTES", "ELAPSED TIME", "CPU TIME"]

  getServers() {
    return ["yali", "yali2"]
  }

}
