import React from "react";
import { Card, Accordion as RBAccordion } from "react-bootstrap";
import Toggle from "../toggle/Toggle";

const AccordionContent = props => {
  const cb = () => {};
  const header = props.header;
  const body = props.body;

  return (
    <Card className="accordion-card" key={header}>
      <Toggle eventKey={header} callback={cb}>
        {header}
      </Toggle>
      <RBAccordion.Collapse eventKey={header}>
        <Card.Body>{body}</Card.Body>
      </RBAccordion.Collapse>
    </Card>
  );
};

export default AccordionContent;
