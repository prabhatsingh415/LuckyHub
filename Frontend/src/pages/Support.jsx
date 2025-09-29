import React from "react";
import { Mail } from "lucide-react"; // Using Lucide icon as example

function Support() {
  return (
    <div className="min-h-screen bg-white dark:bg-[var(--black)] text-black dark:text-white p-8 md:p-16">
      <h1 className="text-3xl font-bold mb-6">Support</h1>
      <div className="space-y-4 text-lg leading-relaxed">
        <p>Need help? Our support team is here for you:</p>
        <ul className="list-disc pl-6 space-y-2">
          <li>
            <Mail className="inline-block mr-2" /> <strong>Email:</strong>{" "}
            support@luckyhub.com
          </li>
          <li>
            <strong>Response Time:</strong> Typically within 24–48 hours.
          </li>
          <li>
            <strong>FAQs:</strong> Check out our FAQ section for common
            questions.
          </li>
          <li>
            <strong>Feedback:</strong> We welcome suggestions to improve
            LuckyHub.
          </li>
        </ul>
      </div>
    </div>
  );
}

export default Support;
