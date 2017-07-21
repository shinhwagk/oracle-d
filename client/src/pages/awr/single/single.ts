import { Component } from '@angular/core';
import { Http } from '@angular/http'
import { NavController, NavParams } from 'ionic-angular';

import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'page-awr-single',
  templateUrl: 'single.html'
})
export class AwrSinglePage {

  constructor(public navCtrl: NavController, public navParams: NavParams, private http: Http) { }

  getServers() {
    return ["yali", "yali2"]
  }

  public ss: AwrSnapshot[] = []

  displaySnapshots(server, days) {
    const url = `http://localhost:9001/v1/awr/single/snapshots?name=${server}&days=${days}`
    console.info(url)
    return this.http.get(url)
      .toPromise()
      .then(response => this.ss = response.json().data as AwrSnapshot[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }
}

interface AwrSnapshot {
  SNAP_ID: number
  SNAPDAT: string
}
