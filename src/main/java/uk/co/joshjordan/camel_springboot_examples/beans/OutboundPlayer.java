package uk.co.joshjordan.camel_springboot_examples.beans;

import lombok.Data;

@Data
public class OutboundPlayer {
    private String _name;
    private String _clubAndPosition;

    public OutboundPlayer(String name, String clubAndPosition) {

        this._name = name;
        this._clubAndPosition = clubAndPosition;
    }

}
