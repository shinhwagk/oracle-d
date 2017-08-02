import { Component, OnDestroy } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

@Component({
  selector: 'page-keyperformance',
  templateUrl: 'keyperformance.html'

})
export class KeyPerformance implements OnDestroy {

  title = "key performance"

  socket: WebSocket

  instanceInfos: any[]
  sessionInfos: any[]

  constructor(public navCtrl: NavController, public navParams: NavParams) {
    this.socket = new WebSocket('ws://10.65.103.15:9003/v1/keyperformance?name=whdb2');
    this.socket.onmessage = (event) => {
      const data = JSON.parse(event.data)
      if (data['sessInfo']) {
        this.sessionInfos = data['sessInfo']
      } else if (data['instInfo']) {
        this.instanceInfos = data['instInfo']
      }
    }
  }

  ngOnDestroy() {
    if (this.socket) {
      this.socket.send("close")
      this.socket.close()
    }
  }

}