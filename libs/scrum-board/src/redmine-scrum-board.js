      //load the icon of each assignee using gravatar api (the hash is the user email one)
      var hash = {
      "Patrick Lussan" : "82997337aa826114cffe0f9af85928a0",
      "Frank Shipley" : "f51e0d570ee4de62a71df6ce8695b065",
      "David Regnier" : "5f9eca5610b2b5c88873d9d1f92583e2",
      "François Badier": "7bc446e73c201becb3089d35bba0e34f",
      "Claudius Teodorescu" : "9f8c68cbb05cd57e0e30490814965dad"
      };

      $(document).ready(function () {
      $(".assigned_to").each(function(index){
      var name = $(this).text();
      if (name) {
      var
      image = '<img src="http://www.gravatar.com/avatar/' + hash[name] + '?s=20" class="icon"/>';
      $(this).parent().prepend(image);
      }
      });
      });