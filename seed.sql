INSERT INTO agent (agent_name) VALUES ('Clint Barton'),
                                      ('Natasha Romanoff'),
                                      ('Phil Coulson'),
                                      ('Maria Hill'),
                                      ('Daisy Johnson'),
                                      ('Melinda May'),
                                      ('Leo Fitz'),
                                      ('Jemma Simmons'),
                                      ('Grant Ward'),
                                      ('Sharon Carter');

INSERT INTO mission (title, description, status, priority, category, agent_id) VALUES ('Eliminate Black Widow',
                                                                                       'Red Room agent Natasha Romanoff Black Widow has been identified as an eminent threat that needs to be dealt with immediately.',
                                                                                       'active',
                                                                                       'high',
                                                                                       'assassination',
                                                                                       1);